package com.app.restfulws.resource;

import com.app.core.Product;
import com.app.core.RetailController;
import com.app.core.User;
import com.app.core.UserBuyProduct;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Shekhar.Agrawal
 */

@Path("retailDesign")
public class RetailAction {
    
    public RetailAction() {}

    // http://localhost:8080/sample-monolithic-1.0/rest/retailDesign/getallusers
    // http://localhost:8080/sample-monolithic-1.0/rest/retailDesign/getallusers
    @GET
    @Path("/getallusers")
    @Produces("application/json")
    public MyJaxBeanUserList getAllUsers(@Context UriInfo ui) {
        MyJaxBeanUserList mb = null;
        List<User> users = RetailController.getAllUsers();
        mb = new MyJaxBeanUserList("200", "OK", users);
        return mb;
    }

    // http://localhost:8080/sample-monolithic-1.0/rest/retailDesign/getallproducts
    @GET
    @Path("/getallproducts")
    @Produces("application/json")
    public MyJaxBeanProductList getAllProducts(@Context UriInfo ui) {
        MyJaxBeanProductList mb = null;
        List<Product> products = RetailController.getAllProducts();
        mb = new MyJaxBeanProductList("200", "OK", products);
        return mb;
    }

    // http://localhost:8080/sample-monolithic-1.0/rest/retailDesign/getuser?userid=101
    @GET
    @Path("/getuser")
    @Produces("application/json")
    public MyJaxBeanUserList getUser(@Context UriInfo ui) {
        MyJaxBeanUserList mb = null;
        List<User> users = new ArrayList<>();

        MultivaluedMap<String,String> queryParams = ui.getQueryParameters();

        //Check for the Mandatory Parameters else return HTTP 412
        if( queryParams.containsKey("userid") ) {
            String userid = queryParams.getFirst("userid");
            User user = RetailController.getUser(userid);
            users.add(user);
            mb = new MyJaxBeanUserList("200", "OK", users);
        } else {
            // pre condition failed
            mb = new MyJaxBeanUserList("412","Mandatory Parameter 'userid' Missing", users);
        }
        return mb;
    }

    // http://localhost:8080/sample-monolithic-1.0/rest/retailDesign/getproduct?productid=201
    @GET
    @Path("/getproduct")
    @Produces("application/json")
    public MyJaxBeanProductList getProduct(@Context UriInfo ui) {
        MyJaxBeanProductList mb = null;
        List<Product> products = new ArrayList<>();

        MultivaluedMap<String,String> queryParams = ui.getQueryParameters();

        //Check for the Mandatory Parameters else return HTTP 412
        if( queryParams.containsKey("productid") ) {
            String productid = queryParams.getFirst("productid");
            Product product = RetailController.getProduct(productid);
            products.add(product);
            mb = new MyJaxBeanProductList("200", "OK", products);
        } else {
            // pre condition failed
            mb = new MyJaxBeanProductList("412","Mandatory Parameter 'productid' Missing", products);
        }
        return mb;
    }

    // http://localhost:8080/sample-monolithic-1.0/rest/retailDesign/userbuy?userid=101&productid=201
    @GET
    @Path("/userbuy")
    @Produces("application/json")
    public MyJaxBeanUserBuyProduct userBuy(@Context UriInfo ui) {

        MyJaxBeanUserBuyProduct mb = null;
        UserBuyProduct result = new UserBuyProduct();

        MultivaluedMap<String,String> queryParams = ui.getQueryParameters();

        //Check for the Mandatory Parameters else return HTTP 412
        if( queryParams.containsKey("userid") && queryParams.containsKey("productid") ) {
            String userid = queryParams.getFirst("userid");
            String productid = queryParams.getFirst("productid");

            result = RetailController.buyProduct(userid, productid);
            mb = new MyJaxBeanUserBuyProduct("200" , "OK" , result);
        } else {
            // pre condition failed
            mb = new MyJaxBeanUserBuyProduct("412","Mandatory Parameters Missing", result);
        }
        return mb;
    }

}
