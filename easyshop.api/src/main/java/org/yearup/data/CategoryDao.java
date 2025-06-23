package org.yearup.data;

import org.apache.commons.dbcp2.BasicDataSource;
import org.yearup.models.Category;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public interface CategoryDao{

    private BasicDataSource dataSource;

    public CategoryDao(BasicDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<Category> getAllCategories() {
        String sql = "SELECT category_id, name, description FROM categories ORDER BY name;";
        List<Category> categories = new ArrayList<>();

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                int categoryId = resultSet.getInt("category_id");
                String categoryName = resultSet.getString("name");
                String description = resultSet.getString("description");
                categories.add(new Category(categoryId, categoryName, description));
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving categories.");
            e.printStackTrace();
        }

        return categories;
    }

    @Override
    public Category getById(int categoryId) {
        String sql = "SELECT category_id, name, description FROM categories WHERE category_id = ?;";
        Category category = null;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setInt(1, categoryId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    String name = resultSet.getString("name");
                    String description = resultSet.getString("description");
                    category = new Category(categoryId, name, description);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving category by ID", e);
        }

        return category;
    }

    @Override
    public Category create(Category category) {
        String sql = "INSERT INTO categories (name, description) VALUES (?, ?);";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, category.getName());
            preparedStatement.setString(2, category.getDescription());

            int rows = preparedStatement.executeUpdate();
            if (rows == 0) {
                throw new SQLException("Creating category failed, no rows affected.");
            }

            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    category.setCategoryId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating category failed, no ID obtained.");
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error creating new category", e);
        }

        return category;
    }

    @Override
    public void update(int categoryId, Category category) {
        String sql = "UPDATE categories SET name = ?, description = ? WHERE category_id = ?;";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setString(1, category.getName());
            preparedStatement.setString(2, category.getDescription());
            preparedStatement.setInt(3, categoryId);

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error updating category", e);
        }
    }

    @Override
    public void delete(int categoryId) {
        String sql = "DELETE FROM categories WHERE category_id = ?;";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setInt(1, categoryId);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error deleting category", e);
        }
    }
}
