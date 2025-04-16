package six.queenie.model;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.transaction.Transactional;

public interface OrderDetailRepository extends JpaRepository<OrderDetails, Integer> {

	List<OrderDetails> findByOrderId(Integer orderId);

	@Transactional
	default void saveOrderDetails(List<OrderDetails> orderDetailsList, Orders order) {
		for (OrderDetails orderDetails : orderDetailsList) {
			orderDetails.setOrders(order);
		}
		saveAll(orderDetailsList);
	}

	@Query("SELECT YEAR(o.orderDate) as year, MONTH(o.orderDate) as month, p.productType, SUM(od.subTotal) as totalSales "
			+ "FROM OrderDetails od " + "JOIN od.orders o " + "JOIN od.product p " + "WHERE o.orderDate >= :startDate "
			+ "GROUP BY YEAR(o.orderDate), MONTH(o.orderDate), p.productType "
			+ "ORDER BY YEAR(o.orderDate), MONTH(o.orderDate), p.productType")
	List<Object[]> findMonthlySalesByProductType(@Param("startDate") LocalDate startDate);

	@Query("SELECT p.productType, SUM(od.subTotal) as totalSales " 
			+ "FROM OrderDetails od " + "JOIN od.orders o "
			+ "JOIN od.product p " 
			+ "WHERE o.orderDate >= :startDate " 
			+ "GROUP BY p.productType")
	List<Object[]> findTotalSalesByProductType(@Param("startDate") LocalDate startDate);
	

	@Query("SELECT p.productName, SUM(od.quantity) as totalQuantity " +
		       "FROM OrderDetails od " +
		       "JOIN od.product p " +
		       "GROUP BY p.productName " +
		       "ORDER BY totalQuantity DESC " +
		       "LIMIT 5")
    List<Object[]> findTop5BestSellingProducts();
    
    
    @Query("SELECT od FROM OrderDetails od JOIN od.orders o WHERE od.product.productId = :productId AND o.members.memberId = :memberId AND o.status = '已送達'")
    List<OrderDetails> findOrderDetailsByProductIdAndMemberId(@Param("productId") Integer productId, @Param("memberId") Integer memberId);


}
