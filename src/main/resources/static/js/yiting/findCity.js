let globalStores = [];
let globalCity = "";
let globalArea = "";
let globalStreet = "";
// 點選地圖上的縣市後查出區域
function showAdminArea(city) {
    globalStores = [];
    globalCity = city;
    globalArea = "";
    globalStreet = "";
    //上方列出對應的區域選項
    console.log(city);
    var scrollPosition = window.scrollY;
    $("#roadSearchBar").hide();
    $("#taiwanMap").hide();
    $('#searchList').hide();
    $("#chgSearchList").css("display", "block");
    $('#specialList').css("display", "block");
    $(".map_graybox05").css("display", "block");
    $("#showShopList").css("display", "block");
    $("#map").css("display", "none");
    //收起商品選項
    $('#flush-collapseOne').collapse('hide');
    // 清除已勾选项
    $('#selectList input[type="checkbox"]').prop('checked', false);

    // $('#areaResult').css("border", "1px solid #2a9d14");

    const breadcrumbDiv = $(".map_graybox03.result");
    breadcrumbDiv.css("display", "block");

    breadcrumbDiv.find(".breadcrumb-item:nth-child(2) a")
        .unbind("click")
        .text(`${city}`)
        .attr("href", "#")
        .attr("onclick", `showAdminArea('${city}')`)
        .click(function () {
            showAdminArea(city);
        });
    $('#cityLocation').show();

    if ($('#breadStreet').is(':visible')) {
        $('#breadStreet').hide();
    }

    if ($('#searchStreet').is(':visible')) {
        $('#searchStreet').hide();
    }

    if ($('#storeName').is(':visible')) {
        $('#storeName').hide();
    }

    //找到該城市有哪些區
    axios.get('http://localhost:8080/FinalProject/public/front/findAreaBycity', {
        params: {
            city: city
        }
    })
        .then(res => {
            console.log(res.data);
            htmlMakerCity(res.data, city);
            window.scrollTo(0, scrollPosition);
            $('.storeProduct').off('click').on('click', function () {
                let storeProduct = '';

                axios.get('http://localhost:8080/FinalProject/public/front/storeProduct')
                    .then(res => {
                        console.log(res);
                        let data = res.data;
                        data.forEach(function (product) {
                            storeProduct += '<li><input type="checkbox" name="specialItem" productId=' + product.productId + '>' + product.productName;
                            storeProduct += '<img src="/FinalProject/ProductPhoto?productId=' + product.productId + '" class="storeIcon"></li>';
                        });
                        $('#selectList').html(storeProduct);

                        // 將所有生成的 input 元素的 onclick 屬性修改為 newfunction()
                        $('#selectList li input[type="checkbox"]').attr('onclick', 'storeFilterByProduct()');

                    })
                    .catch(err => {
                        console.error(err);
                    });
            });
        })
        .catch(err => {
            console.error(err);
        });


    //店鋪列表
    axios.get('http://localhost:8080/FinalProject/public/front/findBycity', {
        params: {
            city: city
        }
    })
        .then(res => {
            console.log("stores" + res.data);
            globalStores = res.data;
            storeListCity(globalStores);
            window.scrollTo(0, scrollPosition);
        })
        .catch(err => {
            console.error(err);
        });
    //按下左邊秀出商品選項
    $('.storeProduct').on('click', function () {
        let storeProduct = '';

        axios.get('http://localhost:8080/FinalProject/public/front/storeProduct')
            .then(res => {
                console.log(res);
                let data = res.data;
                data.forEach(function (product) {
                    storeProduct += '<li><input type="checkbox" name="specialItem" onclick="storeFilterByProduct()" productId=' + product.productId + '>' + product.productName;
                    storeProduct += '<img src="/FinalProject/ProductPhoto?productId=' + product.productId + '" class="storeIcon"></li>';
                });
                $('#selectList').html(storeProduct);
            })
            .catch(err => {
                console.error(err);
            });
    });



}


//勾選商品後變換畫面(縣市換區域)
function storeFilterByProduct() {
    var scroll = window.scrollY;
    // 檢查被選中的復選框
    console.log(globalStores);
    var selectedItems = [];
    $('input[name="specialItem"]:checked').each(function () {
        selectedItems.push($(this).attr('productid'));
        console.log(selectedItems);
    });

    if (selectedItems.length >= 1) {
        var requestData = {
            stores: globalStores,
            selectedItems: selectedItems
        };

        axios.post('http://localhost:8080/FinalProject/public/front/filterByProduct', requestData)
            .then(function (response) {

                if (response.data.length >= 1) {
                    storeListCity(response.data);
                    let data = response.data;
                    globalCity = data[0].city;
                    // 使用 Set 來確保 area 的唯一性
                    //把符合條件的store的area放進去
                    let uniqueAreas = new Set();
                    data.forEach(store => {
                        uniqueAreas.add(store.area);
                    });

                    // 將 Set 轉換為陣列
                    let uniqueAreasArray = Array.from(uniqueAreas);

                    //傳入區域跟縣市 找出有哪些區域
                    htmlMakerCity(uniqueAreasArray, data[0].city)
                }
                else {
                    $('.searchResult').html("");
                    let storeArea = '';
                    storeArea += '<span>依特定商品查詢</span>';
                    storeArea += '<span class="line02"></span>';
                    storeArea += '<span class="result">查無結果，回<a href="#" onclick="showDefaultContent()">地圖</a>頁查詢</span>'
                    $('#areaResult').html(storeArea);
                }
            })
            .catch(function (error) {
                // 請求失敗處理
                console.error(error);
            });
    } else {
        //依縣市輸出區域
        console.log(globalCity);
        showAdminArea(globalCity);
    }
    window.scrollTo(0, scroll);
}



//依縣市查到的區域結果
function htmlMakerCity(data, city) {
    let storeArea = '';
    storeArea += '<span>依地理位置查詢</span>';
    storeArea += '<span class="line02"></span>';
    storeArea += '<span class="result">從<a href="#" onclick="showDefaultContent()">地圖</a>' + '選擇' + city + '</a>所需查詢區域</span>'
    storeArea += '<ul class="result" id="showTownList">';

    data.forEach(el => {
        console.log(el);
        storeArea += '<li class="search_yellow"><i class="fa-solid fa-store"></i>';
        storeArea += '<a href="#" ';
        storeArea += 'onclick="searchByArea(\'' + city + '\', \'' + el + '\')">' + el + '</a></li>';
    });

    storeArea += '</ul>';

    $('#areaResult').html(storeArea);
}


//商店列表**************************
function storeListCity(data) {
    let storeCities = [];
    let requests = [];

    data.forEach(function (store, index) {
        let storeCity = '<tr onclick="showMap(' + store.storeId + ')" style="cursor:pointer"><td class="graybox">EZBUY' + store.storeName + '</td>';
        storeCity += '<td class="graybox"><table width="100%"><tbody><tr><td> 店舖號：' + store.storeId + '</td>';
        storeCity += '<td align="right"><div class="shop_add_map"><a href="#" onclick="showMap(' + store.storeId + ')"><i class="fa-solid fa-location-dot"></i>';
        storeCity += '<span class="add_map_word">地圖檢視</span></a></div></td></tr></tbody></table>';
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

//單筆結果
function storeOneResult(store) {
    let storeCity = '';

    storeCity += '<tr onclick="showMap(' + store.storeId + ')" style="cursor:pointer"><td class="graybox">EZBUY' + store.storeName + '</td>';
    storeCity += '<td class="graybox"><table width="100%"><tbody><tr><td> 店舖號：' + store.storeId + '</td>';
    storeCity += '<td align="right"><div class="shop_add_map"><a href="#" onclick="showMap(' + store.storeId + ')"><i class="fa-solid fa-location-dot"></i>';
    storeCity += '<span class="add_map_word">地圖檢視</span></a></div></td></tr></tbody></table>';
    storeCity += '地址：' + store.city + store.area + store.street + store.detail + '<br>';
    storeCity += '電話：' + store.tel + '</td><td class="graybox">';

    axios.post('http://localhost:8080/FinalProject/public/front/storeSpecial', store)
        .then(res => {
            if (res.data.length >= 1) {
                console.log(res);
                let data = res.data;
                data.forEach(function (product) {
                    storeCity += '<span class="storeIcon"><img src="/FinalProject/ProductPhoto?productId=' + product + '" class="storeIcon"></span>';
                });
            }
            storeCity += '</td></tr>';
            $('.searchResult').html(storeCity); // 在請求完成後更新 HTML
        })
        .catch(err => {
            console.error(err);
            storeCity += '</td></tr>';
            $('.searchResult').html(storeCity); // 在發生錯誤後更新 HTML
        });
}


//依縣市和區域查街道
function searchByArea(city, area) {

    globalStores = [];
    globalCity = city;
    globalArea = area;
    globalStreet = "";
    console.log(area);
    var scrollPosition = window.scrollY;



    $('#breadStreet').find('a')
        .unbind("click")
        .text(`${area}`)
        .attr("href", "#")
        .attr("onclick", `searchByArea('${city}','${area}')`)
        .click(function () {
            searchByArea(city, area);
        });



    $('#breadStreet').show();
    $("#map").css("display", "none");
    $('#specialList').css("display", "block");
    $('#areaResult').html("");
    $('.searchResult').html("");
    //收起商品選項
    $('#flush-collapseOne').collapse('hide');
    // 清除已勾选项
    $('#selectList input[type="checkbox"]').prop('checked', false);

    if ($('#searchStreet').is(':visible')) {
        $('#searchStreet').hide();
    }
    if ($('#storeName').is(':visible')) {
        $('#storeName').hide();
    }

    axios.get('http://localhost:8080/FinalProject/public/front/findStreetByArea', {
        params: {
            city: city,
            area: area
        }
    })
        .then(res => {
            console.log("街道: " + res.data);
            htmlMakerArea(res.data, city, area);
            window.scrollTo(0, scrollPosition);
        })
        .catch(err => {
            console.error(err);
        });


    //店鋪列表
    axios.get('http://localhost:8080/FinalProject/public/front/findByArea', {
        params: {
            city: city,
            area: area
        }
    })
        .then(res => {
            console.log(res.data);
            globalStores = res.data;
            storeListCity(globalStores)
            window.scrollTo(0, scrollPosition);
            $('.storeProduct').off('click').on('click', function () {
                let storeProduct = '';

                axios.get('http://localhost:8080/FinalProject/public/front/storeProduct')
                    .then(res => {
                        console.log(res);
                        let data = res.data;
                        data.forEach(function (product) {
                            storeProduct += '<li><input type="checkbox" name="specialItem" productId=' + product.productId + '>' + product.productName;
                            storeProduct += '<img src="/FinalProject/ProductPhoto?productId=' + product.productId + '" class="storeIcon"></li>';
                        });
                        $('#selectList').html(storeProduct);

                        // 將所有生成的 input 元素的 onclick 屬性修改為 newfunction()
                        $('#selectList li input[type="checkbox"]').attr('onclick', 'storeFilterByAreaProduct()');

                    })
                    .catch(err => {
                        console.error(err);
                    });
            });
        })
        .catch(err => {
            console.error(err);
        });

}

//勾選商品後變換畫面(區域換街道)
function storeFilterByAreaProduct() {
    var scroll = window.scrollY;
    // 檢查被選中的復選框
    console.log(globalStores);
    var selectedItems = [];
    $('input[name="specialItem"]:checked').each(function () {
        selectedItems.push($(this).attr('productid'));
        console.log(selectedItems);
    });

    if (selectedItems.length >= 1) {
        var requestData = {
            stores: globalStores,
            selectedItems: selectedItems
        };

        axios.post('http://localhost:8080/FinalProject/public/front/filterByProduct', requestData)
            .then(function (response) {

                if (response.data.length >= 1) {
                    storeListCity(response.data);
                    let data = response.data;
                    globalCity = data[0].city;
                    globalArea = data[0].area;
                    // 使用 Set 來確保 street 的唯一性
                    //把符合條件的store的street放進去
                    let uniqueAreas = new Set();
                    data.forEach(store => {
                        uniqueAreas.add(store.street);
                    });

                    // 將 Set 轉換為陣列
                    let uniqueAreasArray = Array.from(uniqueAreas);

                    //傳入區域跟縣市 找出有哪些區域
                    htmlMakerArea(uniqueAreasArray, data[0].city, data[0].area)
                }
                else {
                    $('.searchResult').html("");
                    let storeArea = '';
                    storeArea += '<span>依特定商品查詢</span>';
                    storeArea += '<span class="line02"></span>';
                    storeArea += '<span class="result">查無結果，回<a href="#" onclick="showDefaultContent()">地圖</a>頁查詢</span>'
                    $('#areaResult').html(storeArea);
                }
            })
            .catch(function (error) {
                // 請求失敗處理
                console.error(error);
            });
    } else {
        //依區域輸出街道
        searchByArea(globalCity, globalArea);
    }
    window.scrollTo(0, scroll);
}


//查該區域的街道
function htmlMakerArea(data, city, area) {
    let storeArea = '';
    var storeNum = 0;
    $(".map_graybox05").css("display", "block");
    $('#areaResult').html("");

    axios.get('http://localhost:8080/FinalProject/public/front/countStores', {
        params: {
            city: city,
            area: area
        }
    })
        .then(res => {
            console.log(res.data);
            storeNum = res.data;
            storeArea += '<span class="result">'
            storeArea += '<a href="#" onclick="showAdminArea(\'' + city + '\'' + ');">' + city + '</a>' + area + '的店鋪共有' + storeNum + '家</span>';

            storeArea += '<span class="line02"></span>';
            storeArea += '<span>街道篩選</span>';
            storeArea += '<ul class="result" id="showTownList">';

            data.forEach(el => {
                console.log(el);
                storeArea += '<li class="search_yellow"><i class="fa-solid fa-store"></i>';
                storeArea += '<a href="#" onclick="searchByStreet(\'' + city + '\', \'' + area + '\',\'' + el + '\')">' + el + '</a></li>';
            });

            storeArea += '</ul>';

            console.log(storeArea);

            $('#areaResult').html(storeArea);

        })
        .catch(err => {
            console.error(err);
        });


}

//街道的結果
function searchByStreet(city, area, street) {
    globalStores = [];
    globalCity = city;
    globalArea = area;
    globalStreet = street;
    console.log(street);
    var scrollPosition = window.scrollY;
    $("#map").css("display", "none");
    $('.searchResult').html("");
    //收起商品選項
    $('#flush-collapseOne').collapse('hide');
    // 清除已勾选项
    $('#selectList input[type="checkbox"]').prop('checked', false);

    axios.get('http://localhost:8080/FinalProject/public/front/findByStreet', {
        params: {
            city: city,
            area: area,
            street: street
        }
    })
        .then(res => {
            console.log("街道店鋪: " + res.data);
            let storeArea = '';
            var storeNum = 0;
            axios.get('http://localhost:8080/FinalProject/public/front/countStoreByStreet', {
                params: {
                    city: city,
                    area: area,
                    street: street
                }
            }).then(res => {
                console.log(res.data);
                storeNum = res.data;
                storeArea += '<span class="result">'
                storeArea += '<a href="#" onclick="searchByArea(\'' + city + '\', \'' + area + '\');">' + area + '</a>';
                storeArea += '符合路名為' + street + '的店鋪，共有' + storeNum + '家</span>'


                $('#areaResult').html(storeArea);

            })
                .catch(err => {
                    console.error(err);
                });


            window.scrollTo(0, scrollPosition);
        })
        .catch(err => {
            console.error(err);
        });

    //依街道店鋪列表
    axios.get('http://localhost:8080/FinalProject/public/front/findByStreet', {
        params: {
            city: city,
            area: area,
            street: street
        }
    })
        .then(res => {
            globalStores = res.data;
            storeListCity(globalStores);
            console.log("street" + res.data);
            window.scrollTo(0, scrollPosition);
            $('.storeProduct').off('click').on('click', function () {
                let storeProduct = '';

                axios.get('http://localhost:8080/FinalProject/public/front/storeProduct')
                    .then(res => {
                        console.log(res);
                        let data = res.data;
                        data.forEach(function (product) {
                            storeProduct += '<li><input type="checkbox" name="specialItem" productId=' + product.productId + '>' + product.productName;
                            storeProduct += '<img src="/FinalProject/ProductPhoto?productId=' + product.productId + '" class="storeIcon"></li>';
                        });
                        $('#selectList').html(storeProduct);

                        // 將所有生成的 input 元素的 onclick 屬性修改為 newfunction()
                        $('#selectList li input[type="checkbox"]').attr('onclick', 'storeFilterByStreetProduct()');

                    })
                    .catch(err => {
                        console.error(err);
                    });
            });
        })
        .catch(err => {
            console.error(err);
        });

}

function storeFilterByStreetProduct() {
    var scroll = window.scrollY;
    // 檢查被選中的復選框
    console.log(globalStores);
    var selectedItems = [];
    $('input[name="specialItem"]:checked').each(function () {
        selectedItems.push($(this).attr('productid'));
        console.log(selectedItems);
    });

    if (selectedItems.length >= 1) {
        var requestData = {
            stores: globalStores,
            selectedItems: selectedItems
        };

        axios.post('http://localhost:8080/FinalProject/public/front/filterByProduct', requestData)
            .then(function (response) {

                if (response.data.length >= 1) {
                    storeListCity(response.data);
                    let data = response.data;
                    globalCity = data[0].city;
                    globalArea = data[0].area;
                    globalStreet = data[0].street;


                    let storeArea = '';
                    var storeNum = 0;
                    axios.get('http://localhost:8080/FinalProject/public/front/countStoreByStreet', {
                        params: {
                            city: globalCity,
                            area: globalArea,
                            street: globalStreet
                        }
                    }).then(res => {
                        console.log(res.data);
                        storeNum = res.data;
                        storeArea += '<span class="result">'
                        storeArea += '<a href="#" onclick="searchByArea(\'' + city + '\', \'' + area + '\');">' + area + '</a>';
                        storeArea += '符合路名為' + street + '的店鋪，共有' + storeNum + '家</span>'


                        $('#areaResult').html(storeArea);

                    })
                        .catch(err => {
                            console.error(err);
                        });
                }
                else {
                    $('.searchResult').html("");
                    let storeArea = '';
                    storeArea += '<span>依特定商品查詢</span>';
                    storeArea += '<span class="line02"></span>';
                    storeArea += '<span class="result">查無結果，回<a href="#" onclick="showDefaultContent()">地圖</a>頁查詢</span>'
                    $('#areaResult').html(storeArea);
                }
            })
            .catch(function (error) {
                // 請求失敗處理
                console.error(error);
            });
    } else {
        searchByStreet(globalCity, globalArea, globalStreet);
    }
    window.scrollTo(0, scroll);
}

//用上方搜尋列找城市跟路名
function showCityStreet() {
    var scrollPosition = window.scrollY;
    let inputVal = $('#croadWord').val();
    let city = inputVal.substring(0, 3);
    let street = inputVal.substring(3);
    globalStores = [];
    globalCity = city;
    globalArea = "";
    globalStreet = street;

    axios.get('http://localhost:8080/FinalProject/public/front/findByCityStreet', {
        params: {
            city: city,
            street: street
        }
    })
        .then(res => {
            if (res.data && res.data.length >= 1) {
                // 如果找到結果，執行以下操作
                $("#roadSearchBar").hide();
                $("#taiwanMap").hide();
                $('#searchList').hide();
                $("#chgSearchList").css("display", "block");
                $('#specialList').css("display", "block");
                $(".map_graybox05").css("display", "block");
                $("#showShopList").css("display", "block");
                $("#map").css("display", "none");
                $('#flush-collapseOne').collapse('hide');
                // 清除已勾选项
                $('#selectList input[type="checkbox"]').prop('checked', false);


                const breadcrumbDiv = $(".map_graybox03.result");
                breadcrumbDiv.css("display", "block");
                breadcrumbDiv.find(".breadcrumb-item:nth-child(4) a")
                    .unbind("click")
                    .text(`${inputVal}`)
                    .attr("href", "#")
                    .attr("onclick", "showCityStreet()")
                    .click(function () {
                        showCityStreet();
                    });


                $('#searchStreet').show();
                if ($('#cityLocation').is(':visible')) {
                    $('#cityLocation').hide();
                }

                if ($('#breadStreet').is(':visible')) {
                    $('#breadStreet').hide();
                }

                if ($('#storeName').is(':visible')) {
                    $('#storeName').hide();
                }
                globalStores = res.data;
                storeListCity(res.data);
                $('.storeProduct').off('click').on('click', function () {
                    let storeProduct = '';

                    axios.get('http://localhost:8080/FinalProject/public/front/storeProduct')
                        .then(res => {
                            console.log(res);
                            let data = res.data;
                            data.forEach(function (product) {
                                storeProduct += '<li><input type="checkbox" name="specialItem" productId=' + product.productId + '>' + product.productName;
                                storeProduct += '<img src="/FinalProject/ProductPhoto?productId=' + product.productId + '" class="storeIcon"></li>';
                            });
                            $('#selectList').html(storeProduct);

                            // 將所有生成的 input 元素的 onclick 屬性修改為 newfunction()
                            $('#selectList li input[type="checkbox"]').attr('onclick', 'storeFilterByStreetProduct()');

                        })
                        .catch(err => {
                            console.error(err);
                        });
                });


                let storeStreet = '';
                var storeNum = 0;
                axios.get('http://localhost:8080/FinalProject/public/front/countByCityAndStreet', {
                    params: {
                        city: city,
                        street: street
                    }
                }).then(res => {
                    console.log(res.data);
                    storeNum = res.data;
                    storeStreet += '<span class="result">從<a href="#" onclick="showDefaultContent()">查詢首頁</a>'
                    storeStreet += '符合街道路名為' + inputVal + '的店鋪，共有' + storeNum + '家</span>'

                    $('#areaResult').html(storeStreet);

                })
                    .catch(err => {
                        console.error(err);
                    });
                window.scrollTo(0, scrollPosition);
            } else {
                // 如果沒有找到結果，顯示 alert
                alert('查無此縣市及路名，請確定格式符合!');
                window.scrollTo(0, scrollPosition);
                $('#croadWord').val("");
                showDefaultContent();
            }
        })
        .catch(err => {
            console.error(err);
        });
};


//用右邊搜尋郵遞區號
function zipSearch() {
    let zipVal = $('#zip').val();
    globalStores = [];
    globalStreet = "";
    var scroll = window.scrollY;
    axios.get('http://localhost:8080/FinalProject/public/front/findByZip', {
        params: {
            cityNum: zipVal
        }
    })
        .then(res => {
            if (res.data) {
                console.log(res.data[0], res.data[1]);
                $("#roadSearchBar").hide();
                $("#taiwanMap").hide();
                $("#map").css("display", "none");
                $('#searchList').hide();
                $("#chgSearchList").css("display", "block");
                $('#specialList').css("display", "block");
                $(".map_graybox05").css("display", "block");
                $("#showShopList").css("display", "block");
                const breadcrumbDiv = $(".map_graybox03.result");
                breadcrumbDiv.css("display", "block");
                breadcrumbDiv.find(".breadcrumb-item:nth-child(2) a")
                    .unbind("click")
                    .text(`${res.data[0]}`)
                    .attr("href", "#")
                    .attr("onclick", `showAdminArea(${res.data[0]})`)
                    .click(function () {
                        showAdminArea(res.data[0]);
                    });

                globalCity = res.data[0];
                globalArea = res.data[1];

                searchByArea(res.data[0], res.data[1]);
            } else {
                window.scrollTo(0, scroll);
                $('#zip').val("");
                alert('查無此郵遞區號!');
            }

        }).catch(err => {
            console.error(err);
        });
};

//用右邊搜尋店鋪名稱
function findByStoreName() {
    let nameVal = $('#shopName').val();
    globalStores = [];
    globalCity = "";
    globalArea = "";
    globalStreet = "";
    //收起商品選項
    $('#flush-collapseOne').collapse('hide');
    // 清除已勾选项
    $('#selectList input[type="checkbox"]').prop('checked', false);
    var scroll = window.scrollY;

    axios.get('http://localhost:8080/FinalProject/public/front/findByName', {
        params: {
            storeName: nameVal
        }
    })
        .then(res => {
            if (res.data && res.data.length >= 1 && nameVal.length >= 1) {
                // 如果找到結果，執行以下操作
                $("#roadSearchBar").hide();
                $("#taiwanMap").hide();
                $('#searchList').hide();
                $("#map").css("display", "none");
                $("#chgSearchList").css("display", "block");
                $('#specialList').css("display", "block");
                $(".map_graybox05").css("display", "block");
                $("#showShopList").css("display", "block");
                console.log();
                // 麵包屑調整
                const breadcrumbDiv = $(".map_graybox03.result");
                breadcrumbDiv.css("display", "block");
                breadcrumbDiv.find(".breadcrumb-item:nth-child(5) a")
                    .unbind("click")
                    .text(`${nameVal}`)
                    .attr("href", "#")
                    .attr("onclick", "findByStoreName()")
                    .click(function () {
                        findByStoreName();
                    });

                $('#storeName').show();
                if ($('#cityLocation').is(':visible')) {
                    $('#cityLocation').hide();
                }

                if ($('#breadStreet').is(':visible')) {
                    $('#breadStreet').hide();
                }

                if ($('#searchStreet').is(':visible')) {
                    $('#searchStreet').hide();
                }

                globalStores = res.data;

                storeListCity(res.data);

                $('.storeProduct').off('click').on('click', function () {
                    let storeProduct = '';

                    axios.get('http://localhost:8080/FinalProject/public/front/storeProduct')
                        .then(res => {
                            console.log(res);
                            let data = res.data;
                            data.forEach(function (product) {
                                storeProduct += '<li><input type="checkbox" name="specialItem" productId=' + product.productId + '>' + product.productName;
                                storeProduct += '<img src="/FinalProject/ProductPhoto?productId=' + product.productId + '" class="storeIcon"></li>';
                            });
                            $('#selectList').html(storeProduct);

                            // 將所有生成的 input 元素的 onclick 屬性修改為 newfunction()
                            $('#selectList li input[type="checkbox"]').attr('onclick', 'storeFilterByNameProduct()');

                        })
                        .catch(err => {
                            console.error(err);
                        });
                });
                let storeStreet = '';
                var storeNum = 0;
                axios.get('http://localhost:8080/FinalProject/public/front/countByWordName', {
                    params: {
                        storeName: nameVal
                    }
                }).then(res => {
                    console.log(res.data);
                    storeNum = res.data;
                    storeStreet += '<span class="result">從<a href="#" onclick="showDefaultContent()">查詢首頁</a>'
                    storeStreet += '符合店名為"' + nameVal + '"的店舖，共有' + storeNum + '家</span>'

                    $('#areaResult').html(storeStreet);

                })
                    .catch(err => {
                        console.error(err);
                    });
                window.scrollTo(0, scroll);
            } else {
                // 如果沒有找到結果，顯示 alert
                alert('查無符合店名!');
                window.scrollTo(0, scroll);
                $('#shopName').val("");
            }
        })
        .catch(err => {
            console.error(err);
        });
}

function storeFilterByNameProduct() {
    var scroll = window.scrollY;
    // 檢查被選中的復選框
    console.log(globalStores);
    var selectedItems = [];
    $('input[name="specialItem"]:checked').each(function () {
        selectedItems.push($(this).attr('productid'));
        console.log(selectedItems);
    });

    if (selectedItems.length >= 1) {
        var requestData = {
            stores: globalStores,
            selectedItems: selectedItems
        };

        axios.post('http://localhost:8080/FinalProject/public/front/filterByProduct', requestData)
            .then(function (response) {

                if (response.data.length >= 1) {
                    storeListCity(response.data);
                    let data = response.data;


                    let storeArea = '';
                    var storeNum = 0;
                    axios.get('http://localhost:8080/FinalProject/public/front/countByWordName', {
                        params: {
                            storeName: nameVal
                        }
                    }).then(res => {
                        console.log(res.data);
                        storeNum = res.data;
                        storeStreet += '<span class="result">從<a href="#" onclick="showDefaultContent()">查詢首頁</a>'
                        storeStreet += '符合店名為"' + nameVal + '"的店舖，共有' + storeNum + '家</span>'

                        $('#areaResult').html(storeStreet);

                    })
                        .catch(err => {
                            console.error(err);
                        });
                }
                else {
                    $('.searchResult').html("");
                    let storeArea = '';
                    storeArea += '<span>依特定商品查詢</span>';
                    storeArea += '<span class="line02"></span>';
                    storeArea += '<span class="result">查無結果，回<a href="#" onclick="showDefaultContent()">地圖</a>頁查詢</span>'
                    $('#areaResult').html(storeArea);
                }
            })
            .catch(function (error) {
                // 請求失敗處理
                console.error(error);
            });
    } else {
        findByStoreName();
    }
    window.scrollTo(0, scroll);
}

