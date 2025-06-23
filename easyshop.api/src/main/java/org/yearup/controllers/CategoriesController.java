package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.yearup.data.CategoryDao;
import org.yearup.data.ProductDao;
import org.yearup.models.Category;
import org.yearup.models.Product;

import java.util.ArrayList;
import java.util.List;

// add the annotations to make this a REST controller
// add the annotation to make this controller the endpoint for the following url
    // http://localhost:8080/categories
// add annotation to allow cross site origin requests

@RestController
public class CategoriesController
{
    private CategoryDao categoryDao;
    private ProductDao productDao;


    // create an Autowired controller to inject the categoryDao and ProductDao
    @Autowired
    public CategoriesController(CategoryDao categoryDao, ProductDao productDao) {
        this.categoryDao = categoryDao;
        this.productDao = productDao;
    }

    // add the appropriate annotation for a get action
    @GetMapping(path="/categories")
    public List<Category> getAll()
    {
        // find and return all categories
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT "

        return categories;
    }

    // add the appropriate annotation for a get action
    @GetMapping
    public Category getById(@PathVariable int id)
    {
        // get the category by id
        Category category = categoryDao.getById(id);
        return category;
    }

    // the url to return all products in category 1 would look like this
    // https://localhost:8080/categories/1/products
    @GetMapping("{categoryId}/products")
    public List<Product> getProductsById(@PathVariable int categoryId)
    {
        // get a list of product by categoryId

        return productDao.getProductsByCategoryId(categoryId);
    }

    // add annotation to call this method for a POST action
    // add annotation to ensure that only an ADMIN can call this function
    @PostMapping
    public Category addCategory(@RequestBody Category category)
    {
        // insert the category
        Category category1 = categoryDao.addCategory(category);
        return category1;
    }

    // add annotation to call this method for a PUT (update) action - the url path must include the categoryId
    // add annotation to ensure that only an ADMIN can call this function
    @PutMapping
    public void updateCategory(@PathVariable int id, @RequestBody Category category)
    {
        category.setCategoryId(id);
        // update the category by id
        int rowsAffected = categoryDao.updateCategory(category);
    }


    // add annotation to call this method for a DELETE action - the url path must include the categoryId
    // add annotation to ensure that only an ADMIN can call this function
    @DeleteMapping
    public void deleteCategory(@PathVariable int id)
    {
        // delete the category by id
        int rowsAffected = categoryDao.deleteCategory(id);
    }
}
