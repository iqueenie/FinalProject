package six.queenie.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import six.pinhong.model.Product;

@Entity @Table(name = "OrderDetails")
public class OrderDetails {
	  @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Integer orderDetailId;
	    
	    @Column(name = "ORDERID", insertable = false, updatable = false)
	    private Integer orderId;
	    
	    @ManyToOne(fetch = FetchType.LAZY)
	    @JoinColumn(name = "ORDERID")
	    private Orders orders;
	    
	    @ManyToOne(fetch = FetchType.LAZY)
	    @JoinColumn(name = "PRODUCTID") 
	    private Product product;
	    private Integer quantity;
	    private Integer subTotal;
	
	    
	    
		public OrderDetails() {
			
		}
		
		public OrderDetails(int orderDetailId, int orderId, Orders orders, Product product, int quantity,
				int subTotal) {
			super();
			this.orderDetailId = orderDetailId;
			this.orderId = orderId;
			this.orders = orders;
			this.product = product;
			this.quantity = quantity;
			this.subTotal = subTotal;
		}

		public int getSubTotal() {
			return subTotal;
		}

		public void setSubTotal(int subTotal) {
			this.subTotal = subTotal;
		}

		public int getOrderDetailId() {
			return orderDetailId;
		}
		public void setOrderDetailId(int orderDetailId) {
			this.orderDetailId = orderDetailId;
		}
		public int getOrderId() {
			return orderId;
		}
		public void setOrderId(int orderId) {
			this.orderId = orderId;
		}
		public Orders getOrders() {
			return orders;
		}
		public void setOrders(Orders orders) {
			this.orders = orders;
		}
		public Product getProduct() {
			return product;
		}
		public void setProduct(Product product) {
			this.product = product;
		}
		public int getQuantity() {
			return quantity;
		}
		public void setQuantity(int quantity) {
			this.quantity = quantity;
		}
		
		
		
		
	
		
	
	
	
	

}
