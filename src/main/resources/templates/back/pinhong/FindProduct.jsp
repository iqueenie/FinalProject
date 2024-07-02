<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org/">
<head>
<meta http-equiv="Cache-Control"
	content="no-cache, no-store, must-revalidate">
<meta http-equiv="Pragma" content="no-cache">
<meta http-equiv="Expires" content="0">
<title>商品資料</title>
<link rel="stylesheet" href="pinhong/css/style.css" />
</head>
<body>
	<header id="header-container"></header>
	<aside id="aside-container"></aside>
	<main class="content">
	<button class="button" onclick="goBack()">上一頁</button>
	<div class="container">
		<h2>商品資料</h2>
		<form:form method="post" action="Productupdate" modelAttribute="product" enctype="multipart/form-data">
			<table>
				<tr>
					<td>商品編號</td>
					<td><input type="text" name="productId"
						value="${product.productId}" readonly></td>
				</tr>
				<tr>
					<td>商品名稱</td>
					<td><input type="text" name="productName"
						value="${product.productName}"></td>
				</tr>
				<tr>
					<td>商品類型</td>
					<td><select id="productType" name="productType">
							<option value="飲品"
								${product.productType == '飲品' ? 'selected' : ''}>飲品</option>
							<option value="零食"
								${product.productType == '零食' ? 'selected' : ''}>零食</option>
							<option value="泡麵"
								${product.productType == '泡麵' ? 'selected' : ''}>泡麵</option>
							<option value="熟食"
								${product.productType == '熟食' ? 'selected' : ''}>熟食</option>
							<option value="生活用品"
								${product.productType == '生活用品' ? 'selected' : ''}>生活用品</option>
							<option value="菸酒"
								${product.productType == '菸酒' ? 'selected' : ''}>菸酒</option>
							<option value="護理用品"
								${product.productType == '護理用品' ? 'selected' : ''}>護理用品</option>
					</select></td>
				</tr>
				<tr>
					<td>成本</td>
					<td><input type="text" name="productCost"
						value="${product.productCost}"></td>
				</tr>
				<tr>
					<td>售價</td>
					<td><input type="text" name="productPrice"
						value="${product.productPrice}"></td>
				</tr>
				<tr>
					<td>效期</td>
					<td><input type="text" name="productExpirydate"
						value="${product.productExpirydate}"></td>
				</tr>
				<tr>
					<td>描述</td>
					<td><input type="text" name="productDescription"
						value="${product.productDescription}"></td>
				</tr>
				<tr>
					<td>上架</td>
					<td><select id="productPublished" name="productPublished">
							<option value="1"
								${product.productPublished == 1 ? 'selected' : ''}>是</option>
							<option value="0"
								${product.productPublished == 0 ? 'selected' : ''}>否</option>
					</select></td>
				</tr>
				<tr>
					<td><label for="imageInput">選擇圖片:</label></td>
					<td><input type="file" id="imageInput" name="imageFile"
						accept="image/*" style="display: none;"
						onchange="previewImage(event)"> <img id="preview"
						src="${productImage.imageUrl}" alt="Product Image"
						style="width: 300px; height: auto; cursor: pointer;"
						onclick="document.getElementById('imageInput').click()"></td>
				</tr>
			</table>
			<input type="submit" value="確定">
		</form:form>
	</div>

	<!-- JavaScript for image selection -->
	<script>
		function previewImage(event) {
			var input = event.target;
			var preview = document.getElementById('preview');

			if (input.files && input.files[0]) {
				var reader = new FileReader();
				reader.onload = function(e) {
					preview.src = e.target.result;
					preview.style.display = 'block';
				}
				reader.readAsDataURL(input.files[0]);
			}
		}

		function goBack() {
			window.history.back();
		}
		document.getElementById("myform").onsubmit = function(event) {
			// 阻止默认的提交行为
			event.preventDefault();

			// 延迟3秒后执行
			setTimeout(function() {
				alert("上傳成功！");
				// 提交表单
				document.getElementById("myform").submit();
			}, 500);
		};
	</script>
	</main>
	<script src="js/all/headerJs.js"></script>
	<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.1/dist/js/bootstrap.bundle.min.js"
            integrity="sha384-/bQdsTh/da6pkI1MST/rWKFNjaCP5gBSY4sEBT38Q/9RBh9AH40zEOg7Hlq2THRZ"
            crossorigin="anonymous"></script>
	<script src="https://kit.fontawesome.com/2fdc1b1d20.js" crossorigin="anonymous"></script>
</body>
</html>
