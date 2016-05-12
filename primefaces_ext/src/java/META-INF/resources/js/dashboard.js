function dashboardReorder() {
    var newOrder = '';
    $('.ui-dashboard').children().each(function (column) {
        $(this).children().each(function (item) {
            if ($(this).css('display') !== 'none') {
                newOrder += column + ',' + getId($(this).attr('id')) + ',' + item + '|';
            }
        });
    });
    $('input[name="newDashboardOrder"]').val(newOrder.substring(0, newOrder.length - 1));
    //console.log($('input[name="newDashboardOrder"]').val());
}
function getId(uiid) {
    var arr = uiid.split(':');
    return arr[arr.length - 1];
}