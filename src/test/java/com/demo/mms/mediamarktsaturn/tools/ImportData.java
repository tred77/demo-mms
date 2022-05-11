package com.demo.mms.mediamarktsaturn.tools;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.junit.jupiter.api.Test;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class ImportData {


    public static final String CATEGORY_INSERT_QUERY = "INSERT into category(id, name) values ('%d', '%s');\n";
    public static final String CATEGORY_UPDATE_QUERY = "UPDATE category set parent_id = %d, full_parent_path = '%s' where id = %d;\n";
    public static final String PRODUCT_INSERT_QUERY = "INSERT into product(id, name, online_status, long_description, short_description) values ('%d', '%s', '%s', '%s', '%s');\n";

    @Test
    void importAll() throws IOException {

        Map<Long, List<Long>> productToCategoriesMap = new HashMap<>();
        Set<Long> allReferedCategoriesInProducts = new HashSet<>();


        // import categories
        Set<Long> allCategories = generateCategorySqlAndGetAllCategoryIds();

        // import products
        generateProductSql(allReferedCategoriesInProducts, productToCategoriesMap);

        // below leaves not defined categories
        allReferedCategoriesInProducts.removeAll(allCategories);

        // create unknown categories
        FileWriter fileWriter = new FileWriter("src/main/resources/db/changelog/sql/unknownCategories.sql");
        for (Long notDefinedCategoryId : allReferedCategoriesInProducts) {
            fileWriter.write(String.format(CATEGORY_INSERT_QUERY, notDefinedCategoryId, "unknown_" + notDefinedCategoryId));
        }
        fileWriter.close();

        fileWriter = new FileWriter("src/main/resources/db/changelog/sql/product_category.sql");
        for (Map.Entry<Long, List<Long>> longListEntry : productToCategoriesMap.entrySet()) {
            for (Long catId : longListEntry.getValue()) {
                String format = String.format("INSERT into product_category(product_id, category_id) values('%d', '%d');\n", longListEntry.getKey(), catId);
                fileWriter.write(format);
            }
        }
        fileWriter.close();
    }

    Set<Long> generateCategorySqlAndGetAllCategoryIds() throws IOException {

        ExcelReader excelReader = new ExcelReader("src/test/resources/data/categories.xlsx");
        FileWriter fileWriter = new FileWriter("src/main/resources/db/changelog/sql/category.sql");

        Map<Long, Long> childToParentMap = new HashMap<>();

        while (true) {
            List<String> fieldValues = excelReader.nextRow();
            if (fieldValues.isEmpty()) {
                break;
            }

            // get the fields
            Long id = Double.valueOf(fieldValues.get(0)).longValue();
            String name = fieldValues.get(1);
            if (NumberUtils.isParsable(fieldValues.get(2))) {
                Long parentId = Double.valueOf(fieldValues.get(2)).longValue();
                childToParentMap.put(id, parentId);
            }

            // write the row insert into the file
            fileWriter.write(String.format(CATEGORY_INSERT_QUERY, id, name));
        }


        // update categories adding the parents
        for (Map.Entry<Long, Long> childToParentEntry : childToParentMap.entrySet()) {
            StringBuilder fullParentPath = new StringBuilder();
            Long parent = childToParentEntry.getValue();
            Stack<String> ancesstors = new Stack<>();
            while (parent != null) {
                ancesstors.push(parent.toString());
                parent = childToParentMap.get(parent);
            }
            while (!ancesstors.isEmpty()) {
                fullParentPath.append(ancesstors.pop() + ";");
            }
            String sql = String.format(CATEGORY_UPDATE_QUERY, childToParentEntry.getValue(), fullParentPath, childToParentEntry.getKey());
            fileWriter.write(sql);
        }
        fileWriter.close();

        return new HashSet<>(childToParentMap.keySet());
    }

    void generateProductSql(Set<Long> allReferedCategoriesInProducts, Map<Long, List<Long>> productToCategoriesMap) throws IOException {

        ExcelReader excelReader = new ExcelReader("src/test/resources/data/products.xlsx");
        FileWriter fileWriter = new FileWriter("src/main/resources/db/changelog/sql/product.sql");

        long productIdGen = 0;
        while (true) {
            List<String> fieldValues = excelReader.nextRow();
            if (fieldValues.isEmpty()) {
                break;
            }

            // get the fields
            Long id = ++productIdGen;
            String name = fieldValues.get(0);
            String categoryIds = fieldValues.get(1);
            String[] split = categoryIds.split(";");
            List<Long> productCategories = Arrays.stream(split)
                    .map(Double::valueOf)
                    .map(Double::longValue)
                    .collect(Collectors.toList());
            allReferedCategoriesInProducts.addAll(productCategories);
            productToCategoriesMap.put(id, productCategories);
            String onlineStatus = fieldValues.get(2);
            String longDesc = fieldValues.get(3).replace('\'', ' ');
            String shortDesc = fieldValues.get(4).replace('\'', ' ');


            // write the row insert into the file
            fileWriter.write(String.format(PRODUCT_INSERT_QUERY, id, name, onlineStatus, longDesc, shortDesc));
        }

        fileWriter.close();

    }
}
