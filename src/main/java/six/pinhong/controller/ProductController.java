package six.pinhong.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import six.pinhong.model.Product;
import six.pinhong.model.ProductImage;
import six.pinhong.service.ProductService;

@Controller
public class ProductController {

	@Autowired
	private ProductService productService;
	
	// 查全部
	@GetMapping("/private/Product/GetAll")
	public String getAllProudcts(Model m) {

		List<Product> products = productService.findAll();

		m.addAttribute("products", products);

		return "back/pinhong/GetAllProduct";
	}
	
	// 查全部，Ajax
	@GetMapping("/Product/findAllAjax")
	@ResponseBody
	public List<Product> findAllStoresAjax() {
		
		return productService.findAll();
		
	}
	
	// 新增進入點
	@GetMapping("/private/Product/AddProductPage")
	public String productInsertMain() {

		return "back/pinhong/InsertProduct";
	}
	
	// 新增 (傳統方法)
	@PostMapping("/private/Product/InsertProduct")
	public String productInsert(
			@RequestParam String productName,
			@RequestParam String productType,
			@RequestParam Integer productCost,
			@RequestParam Integer productPrice,
			@RequestParam Integer productExpirydate,
			@RequestParam String productDescription,
			@RequestParam Integer productPublished,
			@RequestParam Integer productQuantity,
			@RequestParam MultipartFile imageFile,
			Model m
			) throws IOException{
		
	    List<Product> products = productService.findAll();

	    for (Product orgainProduct : products) {
	        if (productName.equals(orgainProduct.getProductName())) {
	            m.addAttribute("errors", "已有相同產品，請重新輸入");
	            return "back/pinhong/InsertProduct";
	        }
	    }
	    // 這裡將在循環外創建和保存新產品
	    Product product = new Product(productName, productType, productCost, productPrice, productExpirydate, productDescription, productPublished, productQuantity);
	    ProductImage productImage = new ProductImage(imageFile.getBytes());

	    product.setProductImage(productImage);
	    productImage.setProduct(product);

	    productService.insertProduct(product);

	    return "redirect:/private/Product/GetAll";
	};
		

	
	// ---------------------------------- 新增進入點
	@GetMapping("/InsertAllDefaultProduct")
	public String productInsertMain1() {

		return "back/pinhong/InsertAllDefaultProduct";
	}
	// 解決SQL會自動補0的問題，一次新增10張
	@PostMapping("/private/Product/InsertProductForFinalProject")
	public String productInsert(
	        @RequestParam List<String> productName,
	        @RequestParam List<String> productType,
	        @RequestParam List<Integer> productCost,
	        @RequestParam List<Integer> productPrice,
	        @RequestParam List<Integer> productExpirydate,
	        @RequestParam List<String> productDescription,
	        @RequestParam List<Integer> productPublished,
	        @RequestParam List<Integer> productQuantity,
	        @RequestParam List<MultipartFile> imageFiles,
	        Model m
	) throws IOException {
	    for (int i = 0; i < productName.size(); i++) {
	        Product product = new Product(
	            productName.get(i),
	            productType.get(i),
	            productCost.get(i),
	            productPrice.get(i),
	            productExpirydate.get(i),
	            productDescription.get(i),
	            productPublished.get(i),
	            productQuantity.get(i)
	        );
	        ProductImage productImage = new ProductImage(imageFiles.get(i).getBytes());      
	        product.setProductImage(productImage);
	        productImage.setProduct(product);
	        productService.insertProduct(product);
	    }
	    return "redirect:/private/Product/GetAll";
	}
	// ---------------------------------------------------------------------------
	
	
	
	// 刪除，Ajax
	@DeleteMapping("/private/Product/delete")
	@ResponseBody
	public String productDelete(@RequestParam("productId") Integer productId) {
		productService.deleteById(productId);
		return "success";
	}
	
	//顯示圖片
	@GetMapping("/ProductPhoto")
	public ResponseEntity<byte[]> downloadPhotos(@RequestParam Integer productId) {
		
		ProductImage productImage = productService.findImageById(productId);
		
		byte[] imageUrl = productImage.getImageUrl();
		HttpHeaders headers = new HttpHeaders();
		
		headers.setContentType(MediaType.IMAGE_JPEG);
		
		return new ResponseEntity<byte[]>(imageUrl, headers, HttpStatus.OK);	
	}
	
	// 查詢
	@GetMapping("/private/Product/UpdatePage")
	public String productUpdateMain(@RequestParam("productId") Integer productId, Model m) {
		Product product = productService.findProductById(productId);
		ProductImage productImage = productService.findImageById(productId);
		m.addAttribute("product", product);
		m.addAttribute("productImage", productImage);
		return "back/pinhong/FindProduct";
	}

	// 更新 (MVC使用@ModelAttribute自動映射)
	@PutMapping("/private/Product/Update")
	public String productFindById(@ModelAttribute("product") Product product,
			@RequestParam MultipartFile imageFile,
			Model m) throws IOException {

	    List<Product> products = productService.findAll();

	    for (Product orgainProduct : products) {
	        if (product.getProductName().equals(orgainProduct.getProductName())) {
	            m.addAttribute("errors", "已有相同產品，請重新輸入");
	            return "back/pinhong/FindProduct";
	        }
	    }
								

			if (imageFile != null && !imageFile.isEmpty()) {
				ProductImage productImage = new ProductImage();
				productImage.setProductId(product.getProductId());
				productImage.setImageUrl(imageFile.getBytes());
				// 有更新圖片時new一個productImage把ID、圖片set進去
				
				productImage.setProduct(product);
				product.setProductImage(productImage); 
				// 將圖片關聯到產品
					
				productService.insertProduct(product);
			}else {
				productService.insertProduct(product); 
			}
				
		return "redirect:/private/Product/GetAll";
	}
	
	// shop.html - 前台頁碼、查詢
	@GetMapping("/public/Products")
	public String ShowAllProducts(@RequestParam(value = "searchTerm", defaultValue = "") String searchTerm,
								  @RequestParam(value = "productType", defaultValue = "") String productType,
								  @RequestParam(value = "p", defaultValue = "1") Integer pageNum,
							      @RequestParam(value = "sortField", defaultValue = "productId") String sortField,
							      @RequestParam(value = "sortDir", defaultValue = "asc") String sortDir,
	                         Model model) {
        int pageSize = 5;

        Page<Product> page = productService.findByPage(searchTerm, productType, pageNum, sortField, sortDir, pageSize);
        
	        model.addAttribute("page", page);
	        model.addAttribute("searchTerm", searchTerm);
	        model.addAttribute("productType", productType); // 商品種類
	        model.addAttribute("sortField", sortField); // 哪個欄位排序
	        model.addAttribute("sortDir", sortDir); // ASC或DESC


	    return "front/pinhong/shop";
	}
}


	