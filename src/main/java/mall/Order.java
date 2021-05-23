package mall;

import javax.persistence.*;

import org.springframework.beans.BeanUtils;
import java.util.List;
import java.util.Date;

@Entity
@Table(name="Order_table")
public class Order {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    private Long productId;
    private Integer qty;
    private String status;

    @PostPersist
    public void onPostPersist(){
//        OrderPlaced orderPlaced = new OrderPlaced();
//        BeanUtils.copyProperties(this, orderPlaced);
//        orderPlaced.publishAfterCommit();

        //Following code causes dependency to external APIs
        // it is NOT A GOOD PRACTICE. instead, Event-Policy mapping is recommended.

//        mall.external.Product product = new mall.external.Product();
        // mappings goes here
        boolean rslt = OrderApplication.applicationContext.getBean(mall.external.ProductService.class)
            .checkAndModifyStock(String.valueOf(this.getProductId()), this.getQty());

        if(rslt) {
            OrderPlaced orderPlaced = new OrderPlaced();
            BeanUtils.copyProperties(this, orderPlaced);
            orderPlaced.publishAfterCommit();
        }

    }

    @PreUpdate
    public void onPreUpdate(){
        System.out.println("Update Event raised....... !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
    }

    @PreRemove
    public void onPreRemove(){
        //Following code causes dependency to external APIs
        // it is NOT A GOOD PRACTICE. instead, Event-Policy mapping is recommended.

        // Feign Client controller를 선언한 external에 CancellationService를 통해 호출
        mall.external.Cancellation cancellation = new mall.external.Cancellation();
        // mappings goes here
        cancellation.setOrderId(this.getId());
        cancellation.setStatus("Delievery Cancelled due to order cancellation(orderId = " + this.getId() + ") by customer!!!");
        OrderApplication.applicationContext.getBean(mall.external.CancellationService.class)
                .registerCancelledOrder(cancellation);

        OrderCancelled orderCancelled = new OrderCancelled();
        BeanUtils.copyProperties(this, orderCancelled);
        orderCancelled.publishAfterCommit();

    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }
    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
    }
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }




}
