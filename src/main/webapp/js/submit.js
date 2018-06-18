//CONST
var LAT_DEFAULT_SPB = 59.934932;
var LONG_DEFAULT_SPB = 30.333555;
var ZOOM_DEFAULT_SPB = 9;
var ZOOM_DEFAULT_EUROPE = 3;

var map;

ymaps.ready(function () {
    map = new ymaps.Map("map", {
        center: [LAT_DEFAULT_SPB, LONG_DEFAULT_SPB],
        zoom: ZOOM_DEFAULT_SPB
    });
    map.controls.remove('searchControl');
    map.controls.remove('trafficControl');
    map.controls.remove('typeSelector');
   // map.controls.remove('fullscreenControl');
    map.controls.remove('rulerControl');
    // document.getElementsByClassName("ymaps-2-1-56-map ymaps-2-1-56-i-ua_js_yes ymaps-2-1-56-map-bg-ru ymaps-2-1-56-islets_map-lang-ru")[0].style = "width: 35em; height:30em;"

    $("#sectionTop").hide();
});

function setTop(id) {

    var top_div1 = document.getElementById('top1');
    var top_img1 = document.getElementById('img1');
    var top_title1 = document.getElementById('title1');
    var top_rate1 = document.getElementById('rating1');
    var top_id1 = document.getElementById('topId1');

    var top_div2 = document.getElementById('top2');
    var top_img2 = document.getElementById('img2');
    var top_title2 = document.getElementById('title2');
    var top_rate2 = document.getElementById('rating2');
    var top_id2 = document.getElementById('topId2');

    var top_div3 = document.getElementById('top3');
    var top_img3 = document.getElementById('img3');
    var top_title3 = document.getElementById('title3');
    var top_rate3 = document.getElementById('rating3');
    var top_id3 = document.getElementById('topId3');

    $.ajax({
        url: '/rest/user/topCategory',
        type: 'GET',
        data: 'id=' + id,
        success: function (data) {
            var topPlaces = JSON.parse(JSON.stringify(data));
            if (topPlaces[0].categoryId) {
                $("#sectionTop").show();
                top_div1.addEventListener("click", function () {
                    showPlaceFromTop(topPlaces[0].categoryId);
                });
                top_img1.src = getCategoryPicture(topPlaces[0].categoryId);
                top_title1.innerHTML = getCategoryTitle(topPlaces[0].categoryId);
                top_rate1.innerHTML = topPlaces[0].popularity;
                top_id1.innerHTML = topPlaces[0].categoryId;
            }
            if (topPlaces[1]) {
                top_div2.addEventListener("click", function () {
                    showPlaceFromTop(topPlaces[1].categoryId);
                });
                top_img2.src = getCategoryPicture(topPlaces[1].categoryId);
                top_title2.innerHTML = getCategoryTitle(topPlaces[1].categoryId);
                top_rate2.innerHTML = topPlaces[1].popularity;
                top_id2.innerHTML = topPlaces[1].categoryId;
            }
            if (topPlaces[2]) {
                top_div3.addEventListener("click", function(){
                    showPlaceFromTop(topPlaces[2].categoryId);
                });
                top_img3.src = getCategoryPicture(topPlaces[2].categoryId);
                top_title3.innerHTML = getCategoryTitle(topPlaces[2].categoryId);
                top_rate3.innerHTML = topPlaces[2].popularity;
                top_id3.innerHTML = topPlaces[2].categoryId;
            }
        },
        error: function () {
            document.getElementById("sectionTop").style.display = "none"
        }

    });
}

function showPlaceFromTop(category) {
    var pl_title = document.getElementById('pl_title');
    var pl_addr = document.getElementById('pl_address');
    var pl_coords = document.getElementById('pl_coords');

    showLoading();
    map.geoObjects.removeAll();
    navigator.geolocation.getCurrentPosition(function(location) {
        $.ajax({
            url: '/rest/user/suggested',
            type: 'GET',
            data: 'cat='+category+'&lat='+location.coords.latitude+'&lon='+location.coords.longitude,
            success: function (data) {
                hideLoading();
                var markList = JSON.parse(JSON.stringify(data));
                jQuery.each(markList, function (key, value) {
                    var placeMark = new ymaps.Placemark([value.latitude, value.longitude], {
                        balloonContent: value.title}, {
                        iconColor: '#ff0000'
                    });
                    map.geoObjects.add(placeMark);
                    placeMark.events.add('click', function () {
                        hideSelectors();
                        pl_title.innerHTML = value.title;
                        pl_addr.innerHTML = value.address;
                        pl_coords.innerHTML = 'lat: ' + value.latitude + ', lon: ' + value.longitude;
                    });
                });
            },
            error: function () {
                document.getElementById("sectionTop").style.display = "none"
            }

        });
    });
    var placeMark = new ymaps.Placemark([place.latitude, place.longitude], {
        // hintContent: "Latest checkins: " + place.vkCheckins.length,
        balloonContent: place.title
        }, {
        iconColor: '#ff0000'
    });
    map.geoObjects.add(placeMark);

    hideLoading();
}

function getCategoryTitle(id) {
    var categoryTitle = "";
    switch (id) {
        case 1:
            categoryTitle = "Cafe";
            break;
        case 2:
            categoryTitle = "Club";
            break;
        case 3:
            categoryTitle = "Entertainment";
            break;
        case 4:
            categoryTitle = "Bar";
            break;
        case 5:
            categoryTitle = "Movie Theater";
            break;
        case 6:
            categoryTitle = "Hotel";
            break;
        case 7:
            categoryTitle = "Educational Institution";
            break;
        case 8:
            categoryTitle = "Museum";
            break;
        case 9:
            categoryTitle = "Theater";
            break;
        case 10:
            categoryTitle = "Park";
            break;
        default:
            categoryTitle = "None:( Checkin everywhere!";

    }
    return categoryTitle;
}

function getCategoryPicture(id) {
    var categoryPicture = "";
    switch (id) {
        case 1:
            categoryPicture = "https://cdn4.iconfinder.com/data/icons/maps-and-navigation-solid-icons-vol-3/72/131-512.png";
            break;
        case 2:
            categoryPicture = "https://upload.wikimedia.org/wikipedia/commons/thumb/2/26/Beats_Music_logo.svg/1023px-Beats_Music_logo.svg.png";
            break;
        case 3:
            categoryPicture = "Entertainment";
            break;
        case 4:
            categoryPicture = "Bar";
            break;
        case 5:
            categoryPicture = "Movie Theater";
            break;
        case 6:
            categoryPicture = "Hotel";
            break;
        case 7:
            categoryPicture = "Educational Institution";
            break;
        case 8:
            categoryPicture = "Museum";
            break;
        case 9:
            categoryPicture = "Theater";
            break;
        case 10:
            categoryPicture = "Park";
            break;
        default:
            categoryPicture = "None:( Checkin everywhere!";

    }
    return categoryPicture;
}

function getPlaces() {
    var type = document.getElementById("userid").value;
    showLoading();
    $.ajax({
        url: '/rest/user',
        type: 'POST',
        data: 'id=' + type,
        success: function (data) {
            hideLoading();
            setTop(type);

            map.geoObjects.removeAll();
            map.setCenter([LAT_DEFAULT_SPB, LONG_DEFAULT_SPB], ZOOM_DEFAULT_SPB);

            var placeMarksList = [];
            var id = "id";
            var placeList = "placeList";
            showLoading();

            $.ajax({
                url: '/rest/user',
                type: 'GET',
                data: 'id=' + type,
                success: function (data) {
                    hideLoading();
                    var markList = JSON.parse(JSON.stringify(data));
                    var enumeration = 1;
                    jQuery.each(markList, function (key, value) {
                        var placeMark = new ymaps.Placemark([value.latitude, value.longitude], {
                            // hintContent: "Latest checkins: " + value.vkCheckins.length,
                            balloonContent: value.title
                        });
                        map.geoObjects.add(placeMark);
                        placeMark.events.add('click', function () {
                            showSinglePlace(value.id);
                        });

                        var place = {};
                        place[id]=value.id;
                        place[placeList]=placeMark;
                        placeMarksList[enumeration] = place;

                        ++enumeration;
                    });
                }
            });
        }
    });
}

function showSinglePlace(id){
    // alert(id);
    hideSelectors();
    getPlace(id);
}

function hideSelectors() {
    var selectors = $(".selectors");
    var single = $("#single");

    if(selectors.is(':visible')) {
        selectors.hide();
        single.show("slow");
    }
}

function hideSinglePlace() {
    var selectors = $(".selectors");
    var single = $("#single");

    single.hide();
    selectors.show("slow");
}

function getPlace(id) {
    showLoading();
    $.ajax({
        url: '/rest/place',
        type: 'GET',
        data: 'id=' + id,
        success: function (data) {
            hideLoading();
            var place = JSON.parse(JSON.stringify(data));
            var img_container = document.getElementById('avatar');
            var pl_title = document.getElementById('pl_title');
            var pl_addr = document.getElementById('pl_address');
            var pl_coords = document.getElementById('pl_coords');

            if (!place.picture)
                img_container.src = "images/empty.jpg";
            else
                img_container.src = place.picture;

            pl_title.innerHTML = place.title;
            pl_addr.innerHTML = place.address;
            pl_coords.innerHTML = 'lat: ' + place.latitude + ', lon: ' + place.longitude;
        }
    })
}

function showLoading() {
    $(".loading").show();
}

function hideLoading() {
    $(".loading").hide();
}
