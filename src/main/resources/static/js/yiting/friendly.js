$(document).ready(function () {
    $("#twzipcode_ADV").twzipcode({
        zipcodeIntoDistrict: true,
        css: ["city form-control", "town form-control"],
        countyName: "city", // 对应到 JavaBean 的 city
        districtName: "area", // 对应到 JavaBean 的 area
        zipcodeName: "cityNum", // 对应到 JavaBean 的 areaNum
    });

    $('.city').change(function () {
        var selectedCity = $(this).val();
        var selectedTown = $('.town').val();
        if (selectedCity) {
            axios.get('http://localhost:8080/FinalProject/public/front/findByArea', {
                params: {
                    city: selectedCity,
                    area: selectedTown
                }
            })
                .then(res => {
                    if (res.data.length >= 1) {
                        console.log(res);
                        $(".fri_content").css("display", "none");
                        storeListCity(res.data)
                        $("#showShopList").css("display", "block");
                    }
                    else {
                        console.log(selectedCity);
                        alert('查無該區域相關店鋪存在友善商品');
                        $('.city').prop('selectedIndex', 0);
                        $('.town').html('<option>鄉鎮縣市</option>');
                    }
                })
        }
    });
});


//商店列表**************************
function storeListCity(data) {
    let storeCities = [];
    let requests = [];

    data.forEach(function (store, index) {
        let storeCity = '<tr onclick="showMap(' + store.storeId + ')" style="cursor:pointer"><td class="graybox">EZBUY' + store.storeName + '</td>';
        storeCity += '<td class="graybox"><table width="100%"><tbody><tr><td> 店舖號：' + store.storeId + '</td>';
        storeCity += '<td align="right"><div class="shop_add_map"><a href="#" onclick="showMap(' + store.storeId + ')"><i class="fa-solid fa-utensils"></i>';
        storeCity += '<span class="add_map_word">友善商品</span></a></div></td></tr></tbody></table>';
        storeCity += '地址：' + store.city + store.area + store.street + store.detail + '<br>';
        storeCity += '電話：' + store.tel + '</td><td class="graybox">';

        // 在陣列中保留每個 store 的 HTML，等待完成請求後再合併
        storeCities[index] = storeCity;

        let request = axios.post('http://localhost:8080/FinalProject/public/front/storeSpecial', store)
            .then(res => {
                if (res.data.length >= 1) {
                    let productsHtml = '';
                    res.data.forEach(function (product) {
                        productsHtml += '<span class="storeIcon"><img src="/FinalProject/ProductPhoto?productId=' + product + '" class="storeIcon"></span>';
                    });
                    // 將產品圖片添加到對應的 storeCity 中
                    storeCities[index] += productsHtml + '</td></tr>';
                } else {
                    storeCities[index] += '</td></tr>';
                }
            })
            .catch(err => {
                console.error(err);
                storeCities[index] += '</td></tr>';
            });

        requests.push(request);
    });

    Promise.all(requests).then(() => {
        // 將所有 storeCities 合併成一個字串
        let storeCityHtml = storeCities.join('');
        $('.searchResult').html(storeCityHtml);
    });
}