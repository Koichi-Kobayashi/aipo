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
           if (Name.value != ""){
                var tr_element = document.createElement('tr');
                tr_element.className = "add";
                tr_element.id = document.getElementById('holidayDate').value;
                tr_element.setAttribute('data-year', Year.value);
//                tr_element.id = String(Year.Value) + String(Month.Value) + String(Day.Value);
                tr_element.setAttribute('data-month', Month.value);
                tr_element.setAttribute('data-day', Day.value);
        var holidayDateString = Year.value + "年" + Month.value + "月" + Day.value +"日";

                tr_element.innerHTML = '<td><div>' + holidayDateString + '</div></td><td><div>' + Name.value + '</div></td><td><input name="name" class="button" type="button" value="削除" onclick="aipo.system.deleteAddElement("' + tr_element.getAttribute('id') + '");" /></td>';
            }
                document.getElementById('holidayName').value = "";
                aipo.system.holidayList[tr_element.getAttribute('id')] = tr_element;;
}

aipo.system.addList = function(viewYear) {
	  var parent_object = document.getElementById('holidayListTable');
	  var reglist = [];
	  reglist = document.getElementsByClassName('registered');
	 for (var i = 0; i < aipo.system.arraylist.length; i++){
		 if (viewYear == aipo.system.arraylist[i].getAttribute('data-year')){
	         var addDate = new Date(aipo.system.arraylist[i].getAttribute('data-year') , aipo.system.arraylist[i].getAttribute('data-month'), aipo.system.arraylist[i].getAttribute('data-day'));
	         var reference = null;
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
	};
