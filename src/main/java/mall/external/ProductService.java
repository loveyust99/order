
package mall.external;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;

//@FeignClient(name="product", url="http://localhost:8081")
@FeignClient(name="product", url="http://{api.product.url}")
public interface ProductService {

    @RequestMapping(method= RequestMethod.GET, path="/products/checkAndModifyStock")
    public boolean checkAndModifyStock(@RequestParam("productId") String productId,
                                    @RequestParam("qty") int qty);

/*
    @RequestMapping(method= RequestMethod.PATCH, path="/products")
    public void checkAndModifyStock(@RequestBody Product product);
*/
}