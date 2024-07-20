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
            alert('查無該區域相關店鋪');
            showDefaultContent();
        }


    });

    $('.town').change(function () {
        var selectedCity = $('.city').val();
        var selectedTown = $(this).val();
        cityGlobal = selectedCity;
        areaGlobal = selectedTown;


        axios.get('http://localhost:8080/FinalProject/front/friByCityAndArea', {
            params: {
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
                    alert('查無該區域相關店鋪');
                    showDefaultContent();
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
        let storeCity = '<tr><td class="graybox">EZBUY' + store.storeName + '</td>';
        storeCity += '<td class="graybox"><table width="100%"><tbody><tr><td> 店舖號：' + store.storeId + '</td>';
        storeCity += '<td align="right" id="friendlyText">';
        storeCity += '<a href="/FinalProject/public/front/friendlyResult?storeId=' + store.storeId + '" style="text-decoration: none; color: inherit;">'
        storeCity += '<i class="fa-solid fa-utensils"></i>'
        storeCity += '<span class="add_map_word">點我看友善商品</span></a></div></td></tr></tbody></table>';
        storeCity += '地址：' + store.city + store.area + store.street + store.detail + '<br>';
        storeCity += '電話：' + store.tel + '</td><td class="graybox prodType" style="width:165px;line-height: 1.5;">';

        // 在陣列中保留每個 store 的 HTML，等待完成請求後再合併
        storeCities[index] = storeCity;


        //回傳該店有的Map{商品類別,數量}
        let request = axios.post('http://localhost:8080/FinalProject/public/front/friendlyProduct', store)
            .then(res => {
                let productsHtml = "";
                $(".fri_content").css("display", "none");
                $("#showShopList").css("display", "block");

                for (let [key, value] of Object.entries(res.data)) {
                    let keyType = `${key}`;
                    let encodedKey = encodeURIComponent(keyType);
                    if (keyType === "飲品") {
                        productsHtml += `<a href="/FinalProject/public/front/friendlyResult?storeId=${store.storeId}&productType=${encodedKey}">
                                    <i class="fa-solid fa-mug-saucer"></i> (${keyType}): ${value}
                                  </a><br>`;
                    } else if (keyType === "零食") {
                        productsHtml += `<a href="/FinalProject/public/front/friendlyResult?storeId=${store.storeId}&productType=${encodedKey}">
                        <i class="fa-solid fa-cookie-bite"></i> (${key}): ${value}</a><br>`
                    } else if (keyType === "泡麵") {
                        productsHtml += `<a href="/FinalProject/public/front/friendlyResult?storeId=${store.storeId}&productType=${encodedKey}">
                         <i class="fa-solid fa-bacon"></i> (${key}): ${value}</a><br>`
                    } else if (keyType === "熟食") {
                        productsHtml += `<a href="/FinalProject/public/front/friendlyResult?storeId=${store.storeId}&productType=${encodedKey}">
                         <i class="fa-solid fa-bowl-food"></i> (${key}): ${value}</a><br>`
                    } else {
                        productsHtml += `<a href="/FinalProject/public/front/friendlyResult?storeId=${store.storeId}&productType=${encodedKey}">
                         <i class="fa-solid fa-circle-question"></i> (其他): ${value}</a><br>`
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
        let currentPage = data.number + 1
        let msg_data = "";
        // msg_data += '<li class="page-item disabled"><a class="page-link bg-none border-0" href="#" aria-label="Previous"><span aria-hidden="true">&laquo;</span></a></li>'
        if (currentPage === 1) {
            msg_data += '<li class="page-item disabled"><a class="page-link bg-none border-0" href="#" aria-label="Previous"><span aria-hidden="true">&laquo;</span></a></li>';
        } else {
            msg_data += '<li class="page-item"><a class="page-link bg-none border-0" href="#" aria-label="Previous" onclick="loadThatPage(' + (currentPage - 1) + ', \'' + cityGlobal + '\', \'' + areaGlobal + '\')"><span aria-hidden="true">&laquo;</span></a></li>';
            console.log(msg_data);
        }

        for (var i = 1; i <= totalPages; i++) {
            if (currentPage === i) {
                msg_data += '<li class="page-item active" data-pageid="' + i + '"><a class="page-link border-0 pageBtn" href="#">' + i + '</a></li>';
            } else {
                msg_data += '<li class="page-item" data-pageid="' + i + '"><a class="page-link border-0 pageBtn" href="#" onclick="loadThatPage(' + i + ', \'' + cityGlobal + '\', \'' + areaGlobal + '\')">' + i + '</a></li>';
            }
        }

        if (currentPage === totalPages) {
            msg_data += '<li class="page-item disabled"><a class="page-link border-0" href="#" aria-label="Next"><span aria-hidden="true">&raquo;</span></a></li>';
        } else {
            msg_data += '<li class="page-item"><a class="page-link border-0" href="#" aria-label="Next" onclick="loadThatPage(' + (currentPage + 1) + ', \'' + cityGlobal + '\', \'' + areaGlobal + '\')"><span aria-hidden="true">&raquo;</span></a></li>';
        }

        $(".pageul").html(msg_data);

        let pageBtns = $('.pageBtn');

        pageBtns.each(function (index) {
            $(this).attr("onclick", `loadThatPage(${index + 1},"${cityGlobal}","${areaGlobal}")`);
        });
    });

    $("#storeResultList").css("display", "none");
    $("#chgSearchList").css("display", "block");
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

