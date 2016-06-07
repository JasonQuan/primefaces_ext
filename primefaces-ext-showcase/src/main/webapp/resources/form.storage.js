//当载入时检查是否有修改，有修改提示载入修改
//绑定输入元素更改事件
//当更改时存入localStorage
//保存后清空form
(function ($) {
    $.fn.restoreForm = function () {
        var fieldKey = 'form$' + document.location.pathname + '$';
        for (var key in localStorage) {
            if (key.indexOf(fieldKey) === 1) {
                console.log('there is local data: ' + key);
                //TODO: restore form or notice user
            }
        }
    };
    
    $.fn.addChangeListener = function () {
        $('input,select').each(function () {
            var fieldKey = 'form$' + document.location.pathname + '$' + $(this).closest('form').attr('id');
            $(this).change(function () {
                var changeItem = JSON.parse(localStorage.getItem(fieldKey));
                if (changeItem === null) {
                    changeItem = {};
                }
                changeItem[$(this).attr('id')] = $(this).val();
                localStorage.setItem(fieldKey, JSON.stringify(changeItem));
                console.log($(this).val());
            });
        });
    };
    
    $.fn.getModifyFields = function (formId) {
        var fieldKey = 'form$' + document.location.pathname + '$' + formId;
        return JSON.parse(localStorage.getItem(fieldKey));
    };

    $.fn.clearLocalForm = function (formId) {
        localStorage.removeItem('form$' + document.location.pathname + '$' + formId);
    };
})(jQuery);