package six.pinhong.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import six.pinhong.model.Product;
import six.pinhong.model.ProductImage;
import six.pinhong.service.ProductService;

@Controller
public class ProductController {

	@Autowired
	private ProductService productService;
	
	// 查全部
	@GetMapping("/GetAllProducts")
	public String getAllProudcts(Model m) {

		List<Product> products = productService.findAll();

		m.addAttribute("products", products);

		return "back/pinhong/GetAllProduct";
	}
	
	// 新增進入點
	@GetMapping("/Productinsertmain")
	public String productInsertMain(Model m) {
		m.addAttribute("product", new Product());
		return "back/pinhong/InsertProduct";
	}
	
	// 新增 (傳統方法)
	@PostMapping("/ProductInsert")
	public String productInsert(
			@RequestParam String productName,
			@RequestParam String productType,
			@RequestParam Integer productCost,
			@RequestParam Integer productPrice,
			@RequestParam Integer productExpirydate,
			@RequestParam String productDescription,
			@RequestParam Integer productPublished,
			@RequestParam MultipartFile imageFile,
			Model m
			) throws IOException{
		
		Product product = new Product(productName,productType,productCost,productPrice,productExpirydate,productDescription,productPublished);
		ProductImage productImage = new ProductImage(imageFile.getBytes());
		
		product.setProductImage(productImage);
		productImage.setProduct(product);
		
		productService.insertProduct(product);

		return "redirect:/GetAllProducts";
	}
	
	
	
	// 刪除
	@GetMapping("/Productdelete")
	public String productDelete(@RequestParam("productId") Integer productId) {
		productService.deleteById(productId);
		return "redirect:/GetAllProducts";
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
	@GetMapping("/ProductUpdate")
	public String productUpdateMain(@RequestParam("productId") Integer productId, Model m) {
		Product product = productService.findProductById(productId);
		ProductImage productImage = productService.findImageById(productId);
		m.addAttribute("product", product);
		m.addAttribute("productImage", productImage);
		return "back/pinhong/FindProduct";
	}

	// 更新 (MVC使用@ModelAttribute自動映射)
	@PutMapping("/Productupdate2")
	public String productFindById(@ModelAttribute("product") Product product,
			@RequestParam MultipartFile imageFile) throws IOException {

								

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
				
		return "redirect:/GetAllProducts";
	}
	
	// 查全部，前台商品頁
	@GetMapping("/ShowAllProducts")
	public String ShowAllProudcts(Model m) {
		
		List<Product> products = productService.findAll();
		
		m.addAttribute("products", products);
		
		return "front/pinhong/AllProductPage";
	}
	
	// 模糊查詢商品
    @GetMapping("/search")
    public String search(@RequestParam(name = "term", required = false) String searchTerm, Model model) {
        List<Product> searchResults;
        if (searchTerm != null && !searchTerm.isEmpty()) {
            searchResults = productService.searchProducts(searchTerm);
        } else {
            searchResults = productService.findAll();
        }
        model.addAttribute("products", searchResults);
        return "front/pinhong/AllProductPage"; // 返回到原始的產品列表頁面
    }
}


	