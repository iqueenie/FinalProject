// 點選地圖上的縣市
function showAdminArea(city) {

    //上方列出對應的區域選項
    console.log(city);
    var scrollPosition = window.scrollY;
    $("#roadSearchBar").hide();
    $("#taiwanMap").hide();
    $(".map_graybox05").css("display", "block");
    $("#showShopList").css("display", "block");

    // $('#areaResult').css("border", "1px solid #2a9d14");

    const breadcrumbDiv = $(".map_graybox03.result");
    breadcrumbDiv.css("display", "block");
    breadcrumbDiv.find(".breadcrumb-item:nth-child(2) a")
        .text(`${city}`)
        .attr("href", "#")
        .click(function (event) {
            showAdminArea(city);
        });
    if ($('#breadStreet').is(':visible')) {
        $('#breadStreet').hide();
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
            console.log(res.data);
            storeListCity(res.data)
            window.scrollTo(0, scrollPosition);
        })
        .catch(err => {
            console.error(err);
        });




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

//依縣市查的商店列表
function storeListCity(data) {
    let storeCity = '';

    data.forEach(function (store) {
        storeCity += '<tr onclick="showMap(' + store.storeId + ')" style="cursor:pointer"><td class="graybox">EZBUY' + store.storeName + '</td>'
        storeCity += '<td class="graybox"><table width="100%"><tbody><tr><td> 店舖號：' + store.storeId + '</td>'
        storeCity += '<td align="right"><div class="shop_add_map"><a href="#"onclick="showMap(' + store.storeId + ')"><span class="add_map_word">地圖檢視</span></a></div></td></tr></tbody></table>'
        storeCity += '地址：' + store.city + store.area + store.street + store.detail + '<br>'
        storeCity += '電話：' + store.tel + '</td >' + '<td class="graybox"><span class="store02">霜淇淋</span>'
        storeCity += '<span class="store04"></span><span class="store10"></span><span class="store39"></span></td></tr >'
    });
    $('.searchResult').html(storeCity);
}



//依縣市和區域查街道
function searchByArea(city, area) {
    console.log(area);
    var scrollPosition = window.scrollY;

    $('#breadStreet').find('a')
        .text(`${area}`)
        .attr("href", "#")
        .click(function (event) {
            searchByArea(city, area);
        });

    $('#breadStreet').show();
    $('#areaResult').html("");
    $('.searchResult').html("");
    // $('#areaResult').removeAttr('style')
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
            storeListCity(res.data)
            window.scrollTo(0, scrollPosition);
        })
        .catch(err => {
            console.error(err);
        });
}





//查該區域的街道
function htmlMakerArea(data, city, area) {
    let storeArea = '';
    var storeNum = 0;
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

            $('#areaResult').html(storeArea);

        })
        .catch(err => {
            console.error(err);
        });


}

//街道的結果
function searchByStreet(city, area, street) {
    console.log(street);
    var scrollPosition = window.scrollY;
    $('.searchResult').html("");

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

    //店鋪列表
    axios.get('http://localhost:8080/FinalProject/public/front/findByStreet', {
        params: {
            city: city,
            area: area,
            street: street
        }
    })
        .then(res => {
            console.log(res.data);
            storeListCity(res.data)
            window.scrollTo(0, scrollPosition);
        })
        .catch(err => {
            console.error(err);
        });

}
