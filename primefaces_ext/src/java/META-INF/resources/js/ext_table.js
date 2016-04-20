function resize() {
    return;
    var $dataBody = $('div.ui-datatable-tablewrapper');
    var dbHeight = $dataBody.css('height');
    $dataBody.css({
        overflow: 'hidden',
        height: dbHeight
    });
}

function bulidNewSort() {
    var newValue = "";
    $('span[class^="column_sort_"]').each(function (index) {
        var eId = $(this).attr('class').replace('column_sort_', '');
        if (newValue.indexOf(eId) === -1) {
            newValue += "," + eId + "-" + index;
        }
    });
    newValue = newValue.replace(",", "");
    // console.log(newValue);
    $('input[name$="_columnOrder"]').val(newValue);
}
function bulidNewWidth() {
    var newWidth = '';
    $('th.ba-column').each(
            function () {
                var thWith = $(this).css('width').replace("px", '');
                newWidth += ","
                        + $(this).attr('class').split('columnId-')[1]
                        .split(' ')[0] + '-' + thWith;
            });
    newWidth = newWidth.replace(",", "");
    $('input[name$="entity_table:newWidth"]').val(newWidth);
}
function removeColumn(columnId) {
    $('.columnId-' + columnId).hide();
}