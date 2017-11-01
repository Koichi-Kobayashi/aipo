/*
 * Aipo is a groupware program developed by TOWN, Inc.
 * Copyright (C) 2004-2015 TOWN, Inc.
 * http://www.aipo.com
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
dojo.provide("aipo.system");

aipo.system.onLoadNetworkInfoDialog = function(portlet_id) {
	var obj = dojo.byId("ipaddress");
	if (obj) {
		obj.focus();
	}
	var forms = document.forms;
	for(var i=0;i<forms.length;i++){
		aimluck.io.disableForm(forms[i], false);
	}
}

aipo.system.onReceiveMessage = function(msg) {
	if (!msg) {
		var arrDialog = dijit.byId("modalDialog");
		if (arrDialog) {
			arrDialog.hide();
		}
		aipo.portletReload('system');
	}
	if (dojo.byId('messageDiv')) {
		dojo.byId('messageDiv').innerHTML = msg;
	}
}

aipo.system.hideDialog = function() {
	var arrDialog = dijit.byId("modalDialog");
	if (arrDialog) {
		arrDialog.hide();
	}
	aipo.portletReload('system');
}

aipo.system.switchAuthSendAdmin = function(check) {
	if (check.value == 2) {
		dojo.byId('smtp_auth_field').style.display = "";
		dojo.byId('pop_auth_field').style.display = "none";
	} else if (check.value == 1) {
		dojo.byId('smtp_auth_field').style.display = "none";
		dojo.byId('pop_auth_field').style.display = "";
	} else {
		dojo.byId('smtp_auth_field').style.display = "none";
		dojo.byId('pop_auth_field').style.display = "none";
	}
}

aipo.system.reloadHolidayList = function(jslink, year) {
	aipo.system.jslink = null;
	aipo.system.year = null;
	aipo.system.holidayListPane = null;
    if (!aipo.system.holidayListPane) {
    	aipo.system.holidayListPane = dijit.byId("holidayListPane");
    	aipo.system.holidayListPane = new aimluck.widget.Contentpane({},"holidayListPane");
    	aipo.system.holidayListPane.onLoad = function() {
            	dojo.byId("holidayList").scrollTop = dojo.byId("holidayListPane").offsetTop;
        }
    }
	aipo.system.jslink = jslink;
	aipo.system.year = year;
    var screen = aipo.system.jslink + "?template=SystemHolidaySettingListScreen&view_year=" + aipo.system.year;
    aipo.system.holidayListPane.viewPage(screen);
}

aipo.system.pagerHolidayList = function(jslink, year) {
	aipo.system.jslink = null;
	aipo.system.year = null;
	aipo.system.jslink = jslink;
	aipo.system.year = year;
    var screen = aipo.system.jslink + "?template=SystemHolidaySettingListScreen&view_year=" + aipo.system.year;
    aipo.system.holidayListPane.viewPage(screen);
}


aipo.system.holidayList = {};
aipo.system.arraylist = [] ;

aipo.system.addHoliday = function(){
    var Year = document.getElementById('holidayDate_year');
    var Month = document.getElementById('holidayDate_month');
    var Day = document.getElementById('holidayDate_day');
    var Name = document.getElementById('holidayName');
    var Date = document.getElementById('holidayDate');
    var ClassName = Year.value + Month.value + Day.value;
    var same_date_flag = false;
    var holidayDateString = Year.value + "年" + Month.value + "月" + Day.value +"日";
       if (Name.value != ""){
        for(var key in aipo.system.holidayList){
            if (Date.value == aipo.system.holidayList[key].getAttribute('id')){
                aipo.system.holidayList[key].innerHTML = '<td><div>' + holidayDateString + '</div></td><td><div>' + Name.value + '</div></td><td><input name="name" class="button" type="button" value="削除" onclick=aipo.system.deleteElement("' + aipo.system.holidayList[key].getAttribute('class') + '"); /></td>';
                same_date_flag = true;
                break;
            }
        }
        if(!same_date_flag){
            var tr_element = document.createElement('tr');
            tr_element.setAttribute('class', 'add' + ClassName);
            tr_element.setAttribute('id', Date.value);
            tr_element.setAttribute('data-year', Year.value);
            tr_element.setAttribute('data-month', Month.value);
            tr_element.setAttribute('data-day', Day.value);


            tr_element.innerHTML = '<td><div>' + holidayDateString + '</div></td><td><div>' + Name.value + '</div></td><td><input name="name" class="button" type="button" value="削除" onclick=aipo.system.deleteElement("' + tr_element.getAttribute('class') + '"); /></td>';
            aipo.system.holidayList[tr_element.getAttribute('class')] = tr_element;
        }
       }
       aipo.system.addHolidayDateValue();
       aipo.system.addHolidayNameValue();
       document.getElementById('holidayName').value = "";

}

aipo.system.addList = function(viewYear) {
    var parent_object = document.getElementById('holidayListTable');
    var reglist = [];
    reglist = document.getElementsByClassName('registered');
    aipo.system.addListSort();
   for (var i = 0; i < aipo.system.arraylist.length; i++){
       if (viewYear == aipo.system.arraylist[i].getAttribute('data-year')){
           var addDate = new Date(aipo.system.arraylist[i].getAttribute('data-year') , aipo.system.arraylist[i].getAttribute('data-month'), aipo.system.arraylist[i].getAttribute('data-day'));
           var reference = null;
        if(reglist.length == 0){
         parent_object.appendChild(aipo.system.arraylist[i]);
        }else{
      for (var j = 0; j < reglist.length; j++){
           var regDate = new Date(reglist[j].getAttribute('data-year') , reglist[j].getAttribute('data-month'), reglist[j].getAttribute('data-day'));

          if (regDate > addDate){
            reference  = reglist[j];
            break;
          } else{

          }
       }
       parent_object.insertBefore(aipo.system.arraylist[i], reference);
   }
       }
   }
}

aipo.system.addListSort = function() {
	aipo.system.arraylist.length = 0;
	for(var key in aipo.system.holidayList){
		aipo.system.arraylist.push(aipo.system.holidayList[key]);
	}

	for(var i = 0;  i  <  aipo.system.arraylist.length - 1; i++){
		for (var j = aipo.system.arraylist.length - 1 ; j > i; j--) {
		var addDate1 = new Date(aipo.system.arraylist[j -1].getAttribute('data-year') , aipo.system.arraylist[j - 1].getAttribute('data-month'), aipo.system.arraylist[j - 1].getAttribute('data-day'));
		var addDate2 = new Date(aipo.system.arraylist[j].getAttribute('data-year') , aipo.system.arraylist[j].getAttribute('data-month'), aipo.system.arraylist[j].getAttribute('data-day'));
		if (addDate1 > addDate2){
			 var tmpEl = aipo.system.arraylist[j];
			 aipo.system.arraylist[j] = aipo.system.arraylist[j-1];
			 aipo.system.arraylist[j-1] = tmpEl;
		} else{

		}
	　}

	}
}

aipo.system.deleteElement = function(className) {
	var node = [];
	node = document.querySelectorAll('.' + className);
	for(var i = 0; i < node.length; i++){

	node[i].parentNode.removeChild(node[i]);

	}
	delete aipo.system.holidayList[className];
	delete aipo.system.holidayDateValue[className];
	delete aipo.system.holidayYearValue[className];
	delete aipo.system.holidayMonthValue[className];
	delete aipo.system.holidayDayValue[className];
	delete aipo.system.holidayNameValue[className];
}

aipo.system.holidayDateValue = {};
aipo.system.holidayNameValue = {};
aipo.system.holidayYearValue = {};
aipo.system.holidayMonthValue = {};
aipo.system.holidayDayValue = {};

aipo.system.addHolidayDateValue = function(){
    var Year = document.getElementById('holidayDate_year');
    var Month = document.getElementById('holidayDate_month');
    var Day = document.getElementById('holidayDate_day');
    var Name = document.getElementById('holidayName');
    var Date = document.getElementById('holidayDate');
    var ClassName = Year.value + Month.value + Day.value;
    var same_date_flag = false;

    var parent = document.getElementById('hiddenDateValue');

       if (Name.value != ""){
        for(var key in aipo.system.holidayDateValue){
            if (Date.value == aipo.system.holidayDateValue[key].getAttribute('data-date')){
                aipo.system.holidayDateValue[key].setAttribute('value', Date.value);
                same_date_flag = true;
            }
        }
        for(var key1 in aipo.system.holidayYearValue){
            if (Date.value == aipo.system.holidayYearValue[key1].getAttribute('data-date')){
                aipo.system.holidayYearValue[key1].setAttribute('value', Year.value);
            }
        }
        for(var key2 in aipo.system.holidayMonthValue){
            if (Date.value == aipo.system.holidayMonthValue[key2].getAttribute('data-date')){
                aipo.system.holidayMonthValue[key2].setAttribute('value', Month.value);
            }
        }
        for(var key3 in aipo.system.holidayDayValue){
            if (Date.value == aipo.system.holidayDayValue[key3].getAttribute('data-date')){
                aipo.system.holidayDayValue[key3].setAttribute('value', Day.value);
            }
        }


        if(!same_date_flag){
            var input_element = document.createElement('input');
            input_element.setAttribute('class', 'add' + ClassName);
            input_element.setAttribute('data-date', Date.value);
            input_element.setAttribute('type', 'hidden');
            input_element.setAttribute('name', 'p_holiday');
            input_element.setAttribute('value', Date.value);

            aipo.system.holidayDateValue[input_element.getAttribute('class')] = input_element;
            parent.appendChild(input_element);

            var input_element_year = document.createElement('input');
            input_element_year.setAttribute('class', 'add' + ClassName);
            input_element_year.setAttribute('data-date', Date.value);
            input_element_year.setAttribute('type', 'hidden');
            input_element_year.setAttribute('name', 'p_holiday_year');
            input_element_year.setAttribute('value', Year.value);

            aipo.system.holidayYearValue[input_element.getAttribute('class')] = input_element_year;
            parent.insertBefore(input_element_year, input_element.nextSibling);

            var input_element_month = document.createElement('input');
            input_element_month.setAttribute('class', 'add' + ClassName);
            input_element_month.setAttribute('data-date', Date.value);
            input_element_month.setAttribute('type', 'hidden');
            input_element_month.setAttribute('name', 'p_holiday_month');
            input_element_month.setAttribute('value', Month.value);

            aipo.system.holidayMonthValue[input_element.getAttribute('class')] = input_element_month;
            parent.insertBefore(input_element_month, input_element_year.nextSibling);

            var input_element_day = document.createElement('input');
            input_element_day.setAttribute('class', 'add' + ClassName);
            input_element_day.setAttribute('data-date', Date.value);
            input_element_day.setAttribute('type', 'hidden');
            input_element_day.setAttribute('name', 'p_holiday_day');
            input_element_day.setAttribute('value', Day.value);

            aipo.system.holidayDayValue[input_element.getAttribute('class')] = input_element_day;
            parent.insertBefore(input_element_day, input_element_month.nextSibling);
        }
       }

}


aipo.system.addHolidayNameValue = function(){
    var Year = document.getElementById('holidayDate_year');
    var Month = document.getElementById('holidayDate_month');
    var Day = document.getElementById('holidayDate_day');
    var Name = document.getElementById('holidayName');
    var Date = document.getElementById('holidayDate');
    var ClassName = Year.value + Month.value + Day.value;
    var same_date_flag = false;

    var parent = document.getElementById('hiddenNameValue');

       if (Name.value != ""){
        for(var key in aipo.system.holidayNameValue){
            if (Date.value == aipo.system.holidayNameValue[key].getAttribute('data-date')){
                aipo.system.holidayNameValue[key].setAttribute('value', Name.value);
                same_date_flag = true;
            }
        }
        if(!same_date_flag){
            var input_element = document.createElement('input');
            input_element.setAttribute('class', 'add' + ClassName);
            input_element.setAttribute('data-date', Date.value);
            input_element.setAttribute('type', 'hidden');
            input_element.setAttribute('name', 'p_holiday_name');
            input_element.setAttribute('value', Name.value);

            aipo.system.holidayNameValue[input_element.getAttribute('class')] = input_element;
            parent.appendChild(input_element);
        }
       }
};