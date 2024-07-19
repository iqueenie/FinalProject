var cityGlobal = "";
var areaGlobal = "";
$(document).ready(function () {
    $("#twzipcode_ADV").twzipcode({
        zipcodeIntoDistrict: true,
        css: ["city form-control", "town form-control"],
        districtInit: true
    });

    $("#twzipcode_ADV").on("change", ".city", function () {

        var city = $(this).val();

        if (city.length >= 1) {
            var $townSelect = $(this).closest(".twzipcode").find(".town");

            // 停留在初始鎮區選項，直到手動選擇鎮區或城市發生變化
            $townSelect.prepend('<option selected value="">鄉鎮縣市</option>');
        } else {
            showDefaultContent();
            alert('查無該區域相關店鋪');
        }
    });



    $('.town').change(function () {
        var selectedCity = $('.city').val();
        var selectedTown = $(this).val();
        cityGlobal = selectedCity;
        areaGlobal = selectedTown;


        axios.get('http://localhost:8080/FinalProject/front/friByCityAndArea', {
            params: {
                p: 1,
                city: selectedCity,
                area: selectedTown
            }
        })
            .then(res => {
                storeListCity(res.data);
                console.log(res.data.content);
                let data = res.data;
                console.log(data.content.length);
                if (data.content.length >= 1) {
                    storeListCity(res.data)
                } else {
                    showDefaultContent();
                    alert('查無該區域相關店鋪');
                }
            })
            .catch(err => {
                console.log(err);
            })

    });
});

//商店列表**************************
function storeListCity(data) {
    var scrollPosition = window.scrollY;
    console.log("top" + scrollPosition);
    let storeCities = [];
    let requests = [];
    data.content.forEach(function (store, index) {
        let storeCity = '<tr onclick="showMap(' + store.storeId + ')" style="cursor:pointer"><td class="graybox">EZBUY' + store.storeName + '</td>';
        storeCity += '<td class="graybox"><table width="100%"><tbody><tr><td> 店舖號：' + store.storeId + '</td>';
        storeCity += '<td align="right"><div class="shop_add_map"><a href="#" onclick="showMap(' + store.storeId + ')"><i class="fa-solid fa-utensils"></i>';
        storeCity += '<span class="add_map_word">友善商品</span></a></div></td></tr></tbody></table>';
        storeCity += '地址：' + store.city + store.area + store.street + store.detail + '<br>';
        storeCity += '電話：' + store.tel + '</td><td class="graybox prodType">';

        // 在陣列中保留每個 store 的 HTML，等待完成請求後再合併
        storeCities[index] = storeCity;


        //回傳該店有的Map{商品類別,數量}
        let request = axios.post('http://localhost:8080/FinalProject/public/front/friendlyProduct', store)
            .then(res => {
                let productsHtml = "";
                $(".fri_content").css("display", "none");
                $("#showShopList").css("display", "block");

                for (let [key, value] of Object.entries(res.data)) {
                    keyType = `${key}`;
                    if (keyType === "飲品") {
                        productsHtml += `<span> <i class="fa-solid fa-mug-saucer"></i>(${key}): ${value}</span>`
                    } else if (keyType === "零食") {
                        productsHtml += `<span> <i class="fa-solid fa-cookie-bite"></i>(${key}): ${value}</span>`
                    } else if (keyType === "泡麵") {
                        productsHtml += `<span> <i class="fa-solid fa-bacon"></i>(${key}): ${value}</span>`
                    } else if (keyType === "熟食") {
                        productsHtml += `<span> <i class="fa-solid fa-bowl-food"></i>(${key}): ${value}</span>`
                    } else {
                        productsHtml += `<span> <i class="fa-solid fa-circle-question"></i>(其他): ${value}</span>`
                    }
                }
                storeCities[index] += productsHtml + '</td></tr>';
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
        let totalPages = data.totalPages
        let msg_data = "";
        msg_data += '<li class="page-item disabled"><a class="page-link bg-none border-0" href="#" aria-label="Previous"><span aria-hidden="true">&laquo;</span></a></li>'
        for (var i = 1; i <= totalPages; i++) {
            msg_data += '<li class="page-item" data-pageid="' + i + '">' + '<a class="page-link border-0 pageBtn" href = "#" > ' + i + '</a ></li > '
        }
        msg_data += '<li class="page-item" ><a class="page-link border-0" href="#" aria-label="Next"><span aria-hidden="true">&raquo;</span></a></li >'
        $(".pageul").html(msg_data);

        let pageBtns = $('.pageBtn');

        pageBtns.each(function (index) {
            $(this).attr("onclick", `loadThatPage(${index + 1},"${cityGlobal}","${areaGlobal}")`);
        });
    });

    console.log("button" + scrollPosition);
    window.scrollTo(0, scrollPosition);
    $("#pageArea").css("display", "block");
}

function loadThatPage(pageId, city, area) {
    var scrollPosition = window.scrollY;
    console.log('有按到' + pageId);


    axios.get('http://localhost:8080/FinalProject/front/friByCityAndArea', {
        params: {
            p: pageId,
            city: city,
            area: area
        }
    })
        .then(res => {
            storeListCity(res.data)

        })
        .catch(err => {
            console.log(err);
        })
    window.scrollTo(0, scrollPosition);

}
