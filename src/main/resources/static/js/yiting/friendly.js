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
            alert('該區域店鋪今日無友善商品');
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
                    alert('該區域店鋪今日無友善商品');
                    showDefaultContent();
                }
            })
            .catch(err => {
                console.log(err);
            })
    });

    let personalLikeHtml = ""
    axios.get('http://localhost:8080/FinalProject/public/front/memberName')
        .then(res => {
            if (res.data) {
                personalLikeHtml += '<a  href="/FinalProject/public/front/personalLike"><h2 style="color: #6BB252; font-weight: bold" class="search_home">' + res.data + '<span style="color: #FAD56A; font-weight: bold">收藏的店鋪<i class="fa-solid fa-heart"></i></span> </h2></a>'
                $("#personalLike").html(personalLikeHtml);
            } else {
                personalLikeHtml += '<a style="color: #6BB252; font-weight: bold" href="/FinalProject/public/frontLoginMain"><h2 style="color: #6BB252; font-weight: bold" class="search_home">立即登入收藏喜愛店鋪 <i class="fa-regular fa-heart"></i></h2></a>';
                $("#personalLike").html(personalLikeHtml);
            }
        }).catch(err => {
            console.error(err);
        });


});

//商店列表**************************
function storeListCity(data) {
    var scrollPosition = window.scrollY;
    console.log("top" + scrollPosition);
    let storeCities = [];
    let requests = [];

    data.content.forEach(function (store, index) {
        // 使用對象存儲每個店鋪的不同部分的 HTML
        storeCities[index] = {
            baseInfo: `<tr><td class="graybox">EZBUY${store.storeName}<br>`,
            likeInfo: '',
            storeInfo: '',
            productsInfo: ''
        };

        let request1 = axios.get('http://localhost:8080/FinalProject/public/front/findLikeByStoreMember', {
            params: {
                storeId: store.storeId
            }
        })
            .then(res => {
                let likeHtml = res.data ?
                    `<a  class="like${store.storeId}" style="color: #FAD56A; font-weight: bold" href="#" onclick="likeChange(this,${store.storeId})" likeAttr="yes"><i class="fa-solid fa-heart"></i>已收藏</a></td>` :
                    `<a class="like${store.storeId}" style="color: #FAD56A; font-weight: bold" href="#" onclick="likeChange(this,${store.storeId})" likeAttr="no"><i class="fa-regular fa-heart"></i>加入收藏</a></td>`;

                // 將結果存儲到對應的 likeInfo 屬性
                storeCities[index].likeInfo = likeHtml;
            })
            .catch(err => {
                console.error(err);
            });

        requests.push(request1);

        let request2 = axios.post('http://localhost:8080/FinalProject/public/front/friendlyProduct', store)
            .then(res => {
                let productsHtml = "";
                $(".fri_content").css("display", "none");
                $("#showShopList").css("display", "block");

                for (let [key, value] of Object.entries(res.data)) {
                    let encodedKey = encodeURIComponent(key);
                    let productTypeIcon;

                    switch (key) {
                        case "飲品":
                            productTypeIcon = '<i class="fa-solid fa-mug-saucer"></i>';
                            break;
                        case "零食":
                            productTypeIcon = '<i class="fa-solid fa-cookie-bite"></i>';
                            break;
                        case "泡麵":
                            productTypeIcon = '<i class="fa-solid fa-bacon"></i>';
                            break;
                        case "熟食":
                            productTypeIcon = '<i class="fa-solid fa-bowl-food"></i>';
                            break;
                        default:
                            productTypeIcon = '<i class="fa-solid fa-circle-question"></i>';
                    }

                    productsHtml += `<a href="/FinalProject/public/front/friendlyResult?storeId=${store.storeId}&productType=${encodedKey}">
                                    ${productTypeIcon} (${key}): ${value}
                                 </a><br>`;
                }

                // 將結果存儲到對應的 productsInfo 屬性
                storeCities[index].productsInfo = productsHtml + '</td></tr>';
            })
            .catch(err => {
                console.error(err);
                storeCities[index].productsInfo = '</td></tr>';
            });

        requests.push(request2);

        // 將基本信息存儲到對應的 storeInfo 屬性
        storeCities[index].storeInfo = `
            <td class="graybox">
                <table width="100%">
                    <tbody>
                        <tr>
                            <td> 店舖號：${store.storeId}</td>
                            <td align="right" id="friendlyText">
                                <a href="/FinalProject/public/front/friendlyResult?storeId=${store.storeId}" style="text-decoration: none; color: inherit;">
                                    <i class="fa-solid fa-utensils"></i>
                                    <span class="add_map_word">查看友善商品</span>
                                </a>
                            </td>
                        </tr>
                    </tbody>
                </table>
                地址：${store.city}${store.area}${store.street}${store.detail}<br>
                電話：${store.tel}
            </td>
            <td class="graybox prodType" style="width:165px;line-height: 1.5;">
        `;
    });

    // 在所有請求完成後一次性拼接 HTML
    Promise.all(requests).then(() => {
        let storeCityHtml = storeCities.map(store =>
            store.baseInfo + store.likeInfo + store.storeInfo + store.productsInfo
        ).join('');

        $('.searchResult').html(storeCityHtml);

        let totalPages = data.totalPages;
        let currentPage = data.number + 1;
        let msg_data = "";

        if (currentPage === 1) {
            msg_data += '<li class="page-item disabled"><a class="page-link bg-none border-0" href="#" aria-label="Previous"><span aria-hidden="true">&laquo;</span></a></li>';
        } else {
            msg_data += `<li class="page-item"><a class="page-link bg-none border-0" href="#" aria-label="Previous" onclick="loadThatPage(${currentPage - 1}, '${cityGlobal}', '${areaGlobal}')"><span aria-hidden="true">&laquo;</span></a></li>`;
        }

        for (let i = 1; i <= totalPages; i++) {
            if (currentPage === i) {
                msg_data += `<li class="page-item active" data-pageid="${i}"><a class="page-link border-0 pageBtn" href="#">${i}</a></li>`;
            } else {
                msg_data += `<li class="page-item" data-pageid="${i}"><a class="page-link border-0 pageBtn" href="#" onclick="loadThatPage(${i}, '${cityGlobal}', '${areaGlobal}')">${i}</a></li>`;
            }
        }

        if (currentPage === totalPages) {
            msg_data += '<li class="page-item disabled"><a class="page-link border-0" href="#" aria-label="Next"><span aria-hidden="true">&raquo;</span></a></li>';
        } else {
            msg_data += `<li class="page-item"><a class="page-link border-0" href="#" aria-label="Next" onclick="loadThatPage(${currentPage + 1}, '${cityGlobal}', '${areaGlobal}')"><span aria-hidden="true">&raquo;</span></a></li>`;
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

function likeChange(element, storeId) {
    let like = $(element).attr('likeAttr');
    var scrollPosition = window.scrollY;
    console.log(like);
    axios.get('http://localhost:8080/FinalProject/front/checkLogging')
        .then(res => {
            if (res.data) {
                if (like === "no") {
                    console.log(like === "no");
                    let likeStatus = $(`.like${storeId}`);
                    likeStatus.html('<i class="fa-solid fa-heart"></i>已收藏');
                    likeStatus.attr('likeAttr', 'yes');
                    window.scrollTo(0, scrollPosition);
                    axios.post('http://localhost:8080/FinalProject/public/front/insertLike', {
                        storeId: storeId
                    })
                        .then(function (response) {
                            console.log(response.data);
                        })
                        .catch(function (error) {
                            console.error(error);
                        });
                } else {
                    console.log(like === "yes");
                    let likeStatus = $(`.like${storeId}`);
                    likeStatus.html('<i class="fa-regular fa-heart"></i>加入收藏');
                    likeStatus.attr('likeAttr', 'no');
                    window.scrollTo(0, scrollPosition);
                    axios.delete('http://localhost:8080/FinalProject/public/front/like/delete', {
                        params: {
                            storeId: storeId
                        }
                    })
                        .then(function (response) {
                        })
                        .catch(function (error) {
                        })
                }
            } else {
                Swal.fire({
                    title: "請先登入",
                    icon: "warning",
                    showCancelButton: true,
                    confirmButtonColor: "green",
                    cancelButtonColor: "gray",
                    confirmButtonText: "登入",
                    cancelButtonText: "取消",
                    reverseButtons: true
                }).then((result) => {
                    if (result.isConfirmed) {
                        window.location.href = "/FinalProject/public/frontLoginMain";
                    }
                });
            }
        })
        .catch(err => {
            console.error(err);
        })
}
