package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.yearup.data.ProductDao;
import org.yearup.data.ShoppingCartDao;
import org.yearup.models.Product;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;

import javax.sql.DataSource;
import java.sql.*;



@Component
public class MySqlShoppingCartDao extends MySqlDaoBase implements ShoppingCartDao
{
    private final ProductDao productDao;
    public MySqlShoppingCartDao(DataSource dataSource, ProductDao productDao)
    {
        super(dataSource);
        this.productDao = productDao;
    }

    @Override
    public ShoppingCart getByUserId(int userId)
    {
        ShoppingCart cart = new ShoppingCart();
        String sql = """
                SELECT product_id, quantity
                FROM shopping_cart
                WHERE user_id = ?;
                """;

        try (Connection conn = getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql))
        {
            preparedStatement.setInt(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next())
            {
                int productId = resultSet.getInt("product_id");
                int quantity = resultSet.getInt("quantity");

                Product product = productDao.getById(productId);
                if (product != null)
                {
                    ShoppingCartItem item = new ShoppingCartItem(product, quantity);
                    cart.add(item);
                }
            }

            return cart;
        }
        catch (SQLException e)
        {
            throw new RuntimeException("Error loading shopping cart for user");
        }
    }

    @Override
    public void addProduct(int userId, int productId)
    {
        String selectSql = "SELECT quantity FROM shopping_cart WHERE user_id = ? AND product_id = ?";
        String insertSql = "INSERT INTO shopping_cart (user_id, product_id, quantity, discount_percent) VALUES (?, ?, 1, 0)";
        String updateSql = "UPDATE shopping_cart SET quantity = quantity + 1 WHERE user_id = ? AND product_id = ?";

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(selectSql))
        {
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, productId);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next())
            {
                try (PreparedStatement prepareStatement = connection.prepareStatement(updateSql))
                {
                    prepareStatement.setInt(1, userId);
                    prepareStatement.setInt(2, productId);
                    prepareStatement.executeUpdate();
                }
            }
            else
            {
                try (PreparedStatement insertStmt = connection.prepareStatement(insertSql))
                {
                    insertStmt.setInt(1, userId);
                    insertStmt.setInt(2, productId);
                    insertStmt.executeUpdate();
                }
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException("Error adding product to cart.", e);
        }
    }

    @Override
    public void updateQuantity(int userId, int productId, int quantity)
    {
        String sql = "UPDATE shopping_cart SET quantity = ? WHERE user_id = ? AND product_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql))
        {
            preparedStatement.setInt(1, quantity);
            preparedStatement.setInt(2, userId);
            preparedStatement.setInt(3, productId);
            preparedStatement.executeUpdate();
        }
        catch (SQLException e)
        {
            throw new RuntimeException("Error updating cart quantity.", e);
        }
    }

    @Override
    public void clearCart(int userId)
    {
        String sql = "DELETE FROM shopping_cart WHERE user_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql))
        {
            preparedStatement.setInt(1, userId);
            preparedStatement.executeUpdate();
        }
        catch (SQLException e)
        {
            throw new RuntimeException("Error clearing cart.", e);
        }
    }
}

