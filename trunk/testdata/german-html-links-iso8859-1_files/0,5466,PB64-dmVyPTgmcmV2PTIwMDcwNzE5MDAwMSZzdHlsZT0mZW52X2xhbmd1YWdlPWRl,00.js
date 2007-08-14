// rev=200707190001 //
SpOnENV_SERVER_IMG = 'http://www.spiegel.de';
SpOnENV_SERVER = 'http://www.spiegel.de';
SpOnENV_FlashvideoPopupParams  = 'width=769,height=489,scrollbars=no,resizable=no,screenX=150,screenY=100';
SpOnENV_FlashPopupParams  = 'scrollbars=no,screenX=150,screenY=100';
SpOnENV_PdfPopupParams    = 'width=600,height=550,scrollbars=no,screenX=150,screenY=100,resizable';
SpOnENV_PopTopPopupParams = 'width=500,height=310,resizable,screenX=150,screenY=100,status=no';
SpOnENV_BigaPopupParams_0   = 'width=625,height=545,scrollbars=auto,resizable,screenX=150,screenY=10';
SpOnENV_BigaPopupParams_1   = 'width=625,height=650,scrollbars=auto,resizable,screenX=150,screenY=10';
SpOnENV_BigaPopupParams_2   = 'width=780,height=650,scrollbars=auto,resizable,screenX=10,screenY=10';
SpOnENV_BigaPopupParams_3   = '';
SpOnENV_BigaPopupParams   = 'width=625,height=545,scrollbars=auto,resizable,screenX=150,screenY=10';
var spVideoCpServerName = 'http://www.spiegel.de';  
function goURL(frmlrname){
adresse = document.forms[frmlrname].to.options[document.forms[frmlrname].to.selectedIndex].value;
if(adresse=="") adresse = "javascript:void(0)";
if(adresse.substr(0,1) == '/') adresse = SpOnENV_SERVER + adresse;
window.location = adresse;
}
function RandomImg (Pfad,aBilder,Ext){
if (typeof(Ext)=='undefined')Ext = "";
if (Pfad =="img"){
Pfad = SpOnENV_SERVER_IMG + "/img/0,1020,";
if(Ext!="") Ext = ",00" + Ext;
} else {
Pfad = SpOnENV_SERVER_IMG + "/static/img/" + Pfad;
}
return Pfad + aBilder[Math.round(Math.random()*(aBilder.length-1))] + Ext;
}
function spon_popup(seite,breite,hoehe,scroll,rsize) {
sbars = (scroll==1)? "yes" : "no";
rsize = (rsize==1)? "yes" : "no";
if(seite.substr(0,1) == '/') seite = SpOnENV_SERVER + seite;
var win_name = breite+hoehe;
var win_attr = "menubar=no,location=no,directories=no,toolbar=no,screenX=0,screenY=0";
win_attr += ",width=" + breite + ",height=" + hoehe + ",scrollbars=" + sbars + ",resizable=" + rsize;
sponWin = open(seite,win_name,win_attr);
sponWin.focus();
}	
function bilderladen(pfad,bildIds,ext) {
if(document.images) {
if(pfad.indexOf("img")==1) {
pfad = pfad + '0,1020,';
ext = ',00' + ext;
}
if(pfad.substr(0,1) == '/') pfad = SpOnENV_SERVER_IMG + pfad;
var bilder = new Array();
for(i=0;i < bildIds.length;i++) {
bilder[i] = pfad + bildIds[i] + ext;
}
chgImg = new Array();
for(i=0;i < bilder.length;i++) {
chgImg[i] = new Image();
chgImg[i].src = bilder[i];
}
}
}
function bildtausch(imgname,imgnr) {
if(document.images) {
if(imgnr < (chgImg.length)) imgname.src = chgImg[imgnr].src;
}
}
function SPONgetCookie (name) 
{
function SPONgetCookieVal (offset) 
{
var endstr = document.cookie.indexOf (";", offset);
if (endstr == -1)
endstr = document.cookie.length;
return unescape(document.cookie.substring(offset, endstr));
}
var arg = name + "=";
var alen = arg.length;
var clen = document.cookie.length;
var i = 0;
while (i < clen) {
var j = i + alen;
if (document.cookie.substring(i, j) == arg)
return SPONgetCookieVal (j);
i = document.cookie.indexOf(" ", i) + 1;
if (i == 0) break;
}
return null;
}
var spMobileClients=[
"midp",
"240x320",
"blackberry",
"netfront",
"nokia",
"panasonic",
"portalmmm",
"sharp",
"sie-",
"sonyericsson",
"symbian",
"windows ce",
"benq",
"mda",
"mot-",
"opera mini",
"philips",
"pocket pc",
"sagem",
"samsung",
"sda",
"sgh-",
"vodafone",
"xda"
];
function spIsMobileClient(userAgent) {
try {
userAgent=userAgent.toLowerCase();
for (var i=0; i < spMobileClients.length; i++)
if (userAgent.indexOf(spMobileClients[i]) != -1)
return true;
}
catch (e) { // pssst.
}
return false;
}
function spIsThisBrowserMobileClient() {
return spIsMobileClient(navigator.userAgent);
}
function spRedirectIfMobileClient() {
try {
if (spIsThisBrowserMobileClient()) {
if (document.location.href.indexOf('#nomobile') != -1) {
document.cookie='nomobile';
return;
}	
if (document.cookie && document.cookie.indexOf('nomobile') != -1) 
return;
document.location.href="http://mobil.spiegel.de?redirect=1";
}
}
catch (e) { 
}
}
function spCounter(url) {
if (typeof(spon_vdz_countframe) != 'undefined') spon_vdz_countframe.location.href = SpOnENV_SERVER + url;
}
function spCounterFlash(flashid) {
spCounter('/flash/count/0,5532,' + flashid + ',00.html');
}
function spCounterContentainer(contentainerid) {
spCounter('/count/contentainer/0,,' + contentainerid + ',00.html');
}
function spCounterVideo(videoid, format) {
var counturl = '/videoplayer/count/' + format + '/0,6298,' + videoid + ',00.html'; 
if (format == 'inline') {
spCounter(counturl);
} else {
return SpOnENV_SERVER + counturl;
}
}
function spStartseitenlink() {
var ausgabe = '';
if (navigator.userAgent.indexOf("Opera") == -1) {
if (navigator.appName.indexOf("Explorer") != -1){
ausgabe = '<a id="spStartseite" href="http://www.spiegel.de/static/startseite/als_startseite_redirect.html" onClick="this.style.behavior=\'url(#default#homepage)\';this.setHomePage(\'http://www.spiegel.de\');" id="spFfsidebar">Startseite<\/a>';
} else if (navigator.userAgent.indexOf("compatible") == -1) {
ausgabe = '<a id="spStartseite" href="javascript:window.open(\'http://www.spiegel.de/static/startseite/anleitung.html\',\'SponStart\',\'width=526,height=660,resizable,screenX=10,screenY=10\').focus();" id="spFfsidebar">Startseite<\/a>';
}
document.write( ausgabe );
}
}
function spGalleryBaseWindow() {
var RightWin = window.opener;
if (RightWin.name.match(/^SPONbiga_(0|1|2)/)) {
window.open('/fotostrecken/0,1518,,00.html','fotostrecken').focus();
} else {
RightWin.location.href='/fotostrecken/0,1518,,00.html';
RightWin.focus();
}
return false;
}
function spBoersenbilderPreload( breite ){
if(document.images){
if (breite == 220) {
var aXName = new Array('dax','tecdax','dow');
} else {
var aXName = new Array('dax','tecdax','dow','nasdaq');
}
aTabImg = new Array();
aChart	= new Array();
for(i=0; i < aXName.length; i++){
aTabImg[i] = new Image(breite,20);
aTabImg[i].src = SpOnENV_SERVER + "/static/sys/boerse/boersenreiter-"+breite+"-"+aXName[i]+".gif";
aChart[i] = new Image (196,91);
aChart[i].src = SpOnENV_SERVER + "/static/sys/boerse/spiegeltrans_"+aXName[i]+".gif";
}
}
}
function spBoerseShowChart(index) {
if(document.images){
document.boerseTab.src = aTabImg[index].src;
document.boerseChart.src = aChart[index].src;
}
}
function spToggleMPC(cid,nr) {
for (i=1;i<=50;i++) {
var mytab = document.getElementById('spMPCTab-'+cid+'-'+i);
if (mytab) {
if (nr == i) {
mytab.className = 'spMPCTab spMPCTabAktiv';
document.getElementById('spMPCContent-'+cid+'-'+i).style.display = 'block';
spCounterContentainer(cid);
} else {
mytab.className = 'spMPCTab';
document.getElementById('spMPCContent-'+cid+'-'+i).style.display = 'none';
}
} else {
break;
}
}
}
var spEmStepWidth 	= 0.125;	// increase/decrease font every step by spEmStepWidth
var spEmBasis 		= 0.875;		// font size of spArticleBody at startup
//var spEmBasis 		= 1;		// font size of spArticleBody at startup
var spEmStep 		= -1;		// counter for current step (leave as 0)
var spEmMaxSteps 	= 1;		// maximum steps alowed
/**
* spFontSizer increase/decrease font size inside "spEmStep"
*
* use: spFontSizer(1, false), spFontSizer(-1, false) or spFontSizer(0, true)
* @param spInc set to 1 or -1
* @param spReset set to true to get initial font size
*/
function spFontSizer(spInc, spReset) {
// reset font size
if (spReset)
spEmStep = -1;
// inside allowed steps?
if (Math.abs(spEmStep + spInc) <= spEmMaxSteps) {
// increase/decrease spEmStep
spEmStep += spInc;
// set new font size for every tag inside "spEmStep"
spEmFontSize = spEmStep * spEmStepWidth + spEmBasis;
//get spArticleBody
spEmBody = document.getElementById('spMainContent');
//		spEmBody = document.getElementsByTagName('body')[0];
// set new fot size
spEmBody.style.fontSize = spEmFontSize + "em";
}
if (spEmStep == -spEmMaxSteps) {
document.getElementById("spFontsizeMinus").src = "/static/sys/v8/icons/spFontsizeMinus_lo.jpg";
}
else if (spEmStep == spEmMaxSteps) {
document.getElementById("spFontsizePlus").src = "/static/sys/v8/icons/spFontsizePlus_lo.jpg";
}
else {
document.getElementById("spFontsizePlus").src = "/static/sys/v8/icons/spFontsizePlus.jpg";
document.getElementById("spFontsizeMinus").src = "/static/sys/v8/icons/spFontsizeMinus.jpg";
}
} 
/******* Social Bookmarking functions ******/
/**
* toggles SocialBookmark-Box on and off
* @param spDisplay - ture=on, false=off
*/
function spSocialBookmarkSetDisplay(spDisplay) {
spSocialBookmarkElement = document.getElementById("spSocialBookmark").style;
spSocialBookmarkElement.visibility = (spDisplay ? "visible" : "hidden");
spSocialBookmarkState=spDisplay;
}
/**
* toggle function called form Bookmark Link
* served spSocialBookmarkSetDisplay() and starts EventHandler
*/
function spSocialBookmarkToggle() {
if (!spSocialBookmarkState) {
spSocialBookmarkSetDisplay(true);
spStartMouseEvent();
}
else 
spSocialBookmarkSetDisplay(false);
}
/**
* Event handler function tests where the user clicked
* and acts	according to this (switch SocialBookmark-Box off 
* stops EventHandler)
*/
function spSocialBookmarkToggleDisplay(e) {
if (!e) var e = window.event;
if (e.target) targ = e.target;
else if (e.srcElement) targ = e.srcElement;
if (targ.nodeType == 3) // defeat Safari bug
targ = targ.parentNode;
if (targ.id == "spSocialBookmarkLink") {
spStopMouseEvent();
return false;
}
var isInDiv = false;
while(targ != null) {
if (targ.id == "spSocialBookmark") {
isInDiv=true;
break;
}
if (targ.id == "spSocialBookmarkClose")
break;
targ = targ.parentNode;
}
if (!isInDiv) {
spSocialBookmarkSetDisplay(false);
spStopMouseEvent();
}
return false;
}
/**
* starts EventHandling
*/
function spStartMouseEvent(){
if (document.addEventListener) { // DOM Level 2 Event Model
document.addEventListener("mouseup", spSocialBookmarkToggleDisplay, true);
}
else if (document.attachEvent) { // IE 5+ Event Model
document.attachEvent("onmouseup", spSocialBookmarkToggleDisplay);
}
else { // IE 4 Event Model
document.onmouseup=spSocialBookmarkToggleDisplay;
}
}
/**
* stops EventHandling
* spOldHandler is a hack for EI4 Event Model
*/
var spOldHandler = document.onmouseup;	// Eventhandler for EI 4 StopEvent
function spStopMouseEvent() {
// Unregister the capturing event handlers.
if (document.removeEventListener) { // DOM Event Model
document.removeEventListener("mouseup", spSocialBookmarkToggleDisplay, true);
}
else if (document.detachEvent) { // IE 5+ Event Model
document.detachEvent("onmouseup", spSocialBookmarkToggleDisplay);
}
else { // IE 4 Event Model
document.onmouseup = spOldHandler;
}
}
var spPaginatorClassesLoaded = Array();
function spPaginatorHandleEvent(spElement) {
spClassName = spElement.parentNode.parentNode.parentNode.id;
spLoadedClass = false;
// check id class is already loaded
for (var key in spPaginatorClassesLoaded) {
if (key == spClassName) {
spLoadedClass = key;
break;
}
}
// if class not loaded create new instance
if (!spLoadedClass) {
spPaginatorClassesLoaded[spClassName] = new spPaginator(spClassName,1);
spLoadedClass = spClassName;
}
// handle click event
if (spElement.className == "spNext")
spPaginatorClassesLoaded[spLoadedClass].showNext(spElement);
else
spPaginatorClassesLoaded[spLoadedClass].showPrev(spElement);
}
var spPaginator = function(paginator_id, index) {
// get container
var container = document.getElementById(paginator_id);
// get pages in the current paginator
var pagesTmp = container.getElementsByTagName('DIV');
this.pages = [];
// get pages and hide them all
for (key=0; key < pagesTmp.length; key++) {
if (pagesTmp[key].className == 'spPaginatorPage' || pagesTmp[key].className == 'spPage') {
var page = pagesTmp[key];
this.pages.push(page);
page.style.display = 'none';
}
}
this.next = true;
this.prev = false;
// make sure default tab-id is valid
this.index = index;
this.index--;
if (this.index < 0 || this.index >= this.pages.length)
this.index = 0;
// show default tab
this.currentPage = this.pages[this.index];
this.currentPage.style.display = 'block';
}
spPaginator.prototype = {
showNext: function(element) {
// remember old index and calculate the new one
if (this.index <= this.pages.length - 1) {
var oldIndex = this.index++;
if (this.index >= this.pages.length)
this.index = 0;
// get new page
var newPage = this.pages[this.index];
// hide old page
this.currentPage.style.display = 'none';
// show new page
this.currentPage = newPage;
this.currentPage.style.display = 'block';
// disable/enable next/prev button
/* 
if (this.prev == false) {
element.parentNode.parentNode.childNodes[0].childNodes[0].childNodes[0].style.display = "block";
this.prev = true;
}
if (this.index == this.pages.length -1) {
element.childNodes[0].style.display = "none";
this.next = false;
}
*/
}
},
showPrev: function(element) {
// remember old index and calculate the new one
if (this.index > 0) {
var oldIndex = this.index--;
if (this.index < 0)
this.index = this.pages.length - 1;
// get new page
var newPage = this.pages[this.index];
// hide old page
this.currentPage.style.display = 'none';
// show new page
this.currentPage = newPage;
this.currentPage.style.display = 'block';
// disable/enable next/prev button
if (this.next == false) {
element.parentNode.parentNode.childNodes[2].childNodes[0].childNodes[0].style.display = "block";
this.next = true;
}
if (this.index == 0) {
element.childNodes[0].style.display = "none";
this.prev = false;
}
}
}
}
var spTmpA;
var spTmpO;
function spMultimediaGalleryImgAttrib()
{
this.imgSet;
this.imgSetArray = new Array();
this.nextPageIndex = 0;
this.isDone = 0;
}
function spMultimediaPaginatorHandleEvent(o,a)
{
spTmpA=a;
spTmpO=o;
window.setTimeout("spMultimediaPaginatorHandleEventDO()", 50);
}
function spMultimediaPaginatorHandleEventDO()
{
var a = spTmpA;
var o = spTmpO;
var multiMediaId = ""+o.parentNode.parentNode.parentNode.id;
var imgElement;
if(a.isDone == 0)
{
for (var i = 0; i < a.imgSetArray[a.nextPageIndex].length; i++)
{
imgElement = document.getElementById(multiMediaId + a.nextPageIndex+i);
if(imgElement != null)
imgElement.src = a.imgSetArray[a.nextPageIndex][i];
}
}
a.nextPageIndex++;
if(a.nextPageIndex >= a.imgSetArray.length)
a.isDone = 1;
spPaginatorHandleEvent(o);
}
var spTmpImgSetToLoad;
// Muss ausserhalb des spEnhPaginators stehen, da per timeout aufgerufen...
function spLoadDeferImgSet(prefix) {
for (var i=0; i < spTmpImgSetToLoad.length; i++) {
var imgElement=document.getElementById(prefix + i);
if (imgElement != null)
imgElement.src = spTmpImgSetToLoad[i];
}
}
var spEnhPaginator = function(paginatorId, imageSets) {
this.pages=[];
this.imageSets=imageSets;
this.completedImageSets=(imageSets != null) ? new Array(imageSets.length) : null;
this.index=0;
this.paginatorId=paginatorId;
this.imageSetToLoad=null;
this.currentPage=null;
this.initDone=false;
this.onChangePage=null;
}
spEnhPaginator.prototype = {
checkInit: function() {
if (!this.initDone) {
var container=document.getElementById(this.paginatorId);
var pagesTmp=container.getElementsByTagName('DIV');
for (key=0; key < pagesTmp.length; key++)
if (pagesTmp[key].className == 'spPaginatorPage')
this.pages.push(pagesTmp[key]);
this.currentPage=this.pages[this.index];
this.initDone=true;
}
},
showNext: function(element) {
this.checkInit();
var oldIndex = this.index++;
if (this.index >= this.pages.length)
this.index = 0;
if (this.onChangePage != null)
this.onChangePage(this.pages[oldIndex]);
this.switchToNewIndex();
},
showPrev: function(element) {
this.checkInit();
var oldIndex = this.index--;
if (this.index < 0)
this.index = this.pages.length - 1;
if (this.onChangePage != null)
this.onChangePage(this.pages[oldIndex]);
this.switchToNewIndex();
},
switchToNewIndex: function(newPage) {
this.currentPage.style.display='none';
this.currentPage=this.pages[this.index];
this.currentPage.style.display='block';
this.checkLoadImages();
},
checkLoadImages: function() {
if (this.imageSets != null) {
var imageSetIndex=this.index-1;
if (this.imageSets[imageSetIndex] != null && !this.completedImageSets[imageSetIndex]) {
spTmpImgSetToLoad=this.imageSets[imageSetIndex];
window.setTimeout("spLoadDeferImgSet('" + this.paginatorId + imageSetIndex + "')", 20);
this.completedImageSets[imageSetIndex]=true;
}
}
}
}
function spVideoGet(videoId) {
if (navigator.appName.indexOf("Microsoft") != -1)
return window[videoId];
else
return document[videoId];
}
function spVpPaginatorOnChangePage(element) {
if (element == null || element.childNodes == null || element.childNodes.length == 0)
return;
var node=element.firstChild;
while (node != null) {
if (node.nodeName.toUpperCase() == "OBJECT") {
var v=spVideoGet(node.id);
if (v != null) {
try {
v.stopVideo();
return;
}
catch(e) {
}
}
}
else if (node.childNodes != null && node.childNodes.length > 0)
spVpPaginatorOnChangePage(node);
node=node.nextSibling;
}
}
function spMainNaviInit() {
if (document.all && document.getElementById) {
var spNavContainer = document.getElementById("spNaviChannel");
var spNavis = spNavContainer.getElementsByTagName('ul');
for (var i in spNavis) {
var ul = spNavis[i];
// wenn 2. Ebene, ...
if (ul.nodeName == 'UL' && ul.className == 'spNaviLevel1') {
var spNavItems = spNavContainer.getElementsByTagName('li');
for (var j in spNavItems) {
var li = spNavItems[j];
//...dann 3. Ebene einblenden, falls vorhanden
if (li.nodeName == 'LI' && li.className.indexOf('spNaviSubNavigation') > -1) {
li.onmouseover = function() {
// lazy initialization
if (! this.spSubNav) {
var spSubNavTmp = this.getElementsByTagName('ul')[0];
if (spSubNavTmp && spSubNavTmp.nodeName == 'UL' && spSubNavTmp.className == 'spNaviLevel2')
this.spSubNav = spSubNavTmp;
}
if (this.spSubNav) {
this.spSubNav.style.display = "block";
}
}
li.onmouseout = function() {
// lazy initialization
if (! this.spSubNav) {
var spSubNavTmp = this.getElementsByTagName('ul')[0];
if (spSubNavTmp && spSubNavTmp.nodeName == 'UL' && spSubNavTmp.className == 'spNaviLevel2')
this.spSubNav = spSubNavTmp;
}
if (this.spSubNav)
this.spSubNav.style.display = "none";
}
}
}
}
}
}
}
function SPONDetectFlashVerInit(){
return spFlashDetectInit();
}
function SPONDetectFlashVer(reqMajorVer, reqMinorVer, reqRevision) {
return spFlashDetect(reqMajorVer, reqMinorVer, reqRevision);
}
function SPONVideoPopup(videoid){
return spOpenFlashvideoPopup(videoid);
}
function OAS_timeseen_onunload() {
return spOasTimeseenOnunload();
}
function setOAS_timeseen_url(url) {
return spOasSetTimeseenUrl(url);
}
function OAS_timeseen(seconds) {
return spOasSetCurrentTimeseen(seconds);
}
function spFlashDetectInit(){
if(!document.getElementById('spFlashDetectInitScript')){
var isIE  = (navigator.appVersion.indexOf("MSIE") != -1) ? true : false;
var isWin = (navigator.appVersion.toLowerCase().indexOf("win") != -1) ? true : false;
var isOpera = (navigator.userAgent.indexOf("Opera") != -1) ? true : false;
if(isIE && isWin && !isOpera){
document.write('<scr'+'ipt id="spFlashDetectInitScript" type="text/vbscript">\n');
document.write('Function spGetSwfVer(i)\n');
document.write('on error resume next\n');
document.write('Dim swControl, swVersion\n');
document.write('swVersion = 0\n');
document.write('set swControl = CreateObject("ShockwaveFlash.ShockwaveFlash." + CStr(i))\n');
document.write('if (IsObject(swControl)) then\n');
document.write('swVersion = swControl.GetVariable("$version")\n');
document.write('end if\n');
document.write('spGetSwfVer = swVersion\n');
document.write('End Function\n');
document.write('<\/scr'+'ipt>\n');
document.write('\n');
} else {
document.write('<scr'+'ipt id="spFlashDetectInitScript" type="text/javascript">\n');
document.write('function spGetSwfVer(i)\n');
document.write('{\n');
document.write('if (navigator.plugins != null && navigator.plugins.length > 0) {\n');
document.write('if (navigator.plugins["Shockwave Flash 2.0"] || navigator.plugins["Shockwave Flash"]) {\n');
document.write('var swVer2 = navigator.plugins["Shockwave Flash 2.0"] ? " 2.0" : "";\n');
document.write('var flashDescription = navigator.plugins["Shockwave Flash" + swVer2].description;\n');
document.write('descArray = flashDescription.split(" ");\n');
document.write('tempArrayMajor = descArray[2].split(".");\n');
document.write('versionMajor = tempArrayMajor[0];\n');
document.write('versionMinor = tempArrayMajor[1];\n');
document.write('if ( descArray[3] != "" ) {tempArrayMinor = descArray[3].split("r");} else {tempArrayMinor = descArray[4].split("r");}\n');
document.write('versionRevision=tempArrayMinor[1] > 0 ? tempArrayMinor[1] : 0;\n');
document.write('// variable flashVer zusammensetzen -> analog zu ie\n');
document.write('flashVer="x " + versionMajor + "," + versionMinor + "," + versionRevision;\n');
document.write('} else {\n');
document.write('flashVer=-1;\n');
document.write('}\n');
document.write('}\n');
document.write('// MSN/WebTV 2.6 supports Flash 4\n');
document.write('else if (navigator.userAgent.toLowerCase().indexOf("webtv/2.6") != -1) flashVer = 4;\n');
document.write('// WebTV 2.5 supports Flash 3\n');
document.write('else if (navigator.userAgent.toLowerCase().indexOf("webtv/2.5") != -1) flashVer = 3;\n');
document.write('// older WebTV supports Flash 2\n');
document.write('else if (navigator.userAgent.toLowerCase().indexOf("webtv") != -1) flashVer = 2;\n');
document.write('// Can\'t detect in all other cases\n');
document.write('else {flashVer = -1;}\n');
document.write('return flashVer;\n');
document.write('}\n');
document.write('<\/scr'+'ipt>\n');
document.write('\n');
}
}
} 
function spFlashDetect(reqMajorVer, reqMinorVer, reqRevision) 
{
var reqVer = parseFloat(reqMajorVer + "." + reqRevision);
// loop backwards through the versions until we find the newest version	
for (var i=25;i>0;i--) {
versionStr = spGetSwfVer(i);
if (versionStr == -1 ) { 
return false;
} else if (versionStr != 0) {
var versionArray = new Array();
var tempArray   = versionStr.split(" ");
var tempString  = tempArray[1];
versionArray    = tempString .split(",");
var versionMajor    = versionArray[0];
var versionMinor    = versionArray[1];
var versionRevision = versionArray[2];
var versionString   = versionMajor + "." + versionRevision;   // 7.0r24 == 7.24
var versionNum      = parseFloat(versionString);
// is the major.revision >= requested major.revision AND the minor version >= requested minor
if ( (versionMajor > reqMajorVer) && (versionNum >= reqVer) ) {
return true;
} else {
return ((versionNum >= reqVer && versionMinor >= reqMinorVer) ? true : false );	
}
}
}
}
function spLoadFlashvideo(videoid, flashvars){
if(document.getElementById){
flashvars += '&FlashVars_JS_setOAS_timeseen_url=spOasSetTimeseenUrl&FlashVars_JS_OAS_timeseen=spOasSetCurrentTimeseen&FlashVars_JS_OAS_reminder=spOasSetReminder&FlashVars_JS_openPopup=spOpenFlashvideoPopup&FlashVars_JS_showFlashDiv=spShowHiddenFlashplayerDiv';
if ( spFlashDetect(8,0,0) ) {
document.getElementById('spFlashvideoMovie'+videoid).innerHTML = '<object classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" codebase="http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab" id="'+videoid+'" width="180" height="155">\n<param name="movie" value="http://www.spiegel.de/static/flash/flashvideo/180-all-006.swf" />\n<param name="quality" value="high" />\n<param name="bgcolor" value="#f2f2f2" />\n<param name="menu" value="false" />\n<param name="allowScriptAccess" value="always" />\n<param name="wmode" value="transparent" />\n<param name="FlashVars" value="'+flashvars+'" />\n<embed src="http://www.spiegel.de/static/flash/flashvideo/180-all-006.swf" name="'+videoid+'" type="application/x-shockwave-flash" width="180" height="155" pluginspage="http://www.macromedia.com/go/getflashplayer" quality="high" bgcolor="#f2f2f2" menu="false" allowScriptAccess="always" wmode="transparent" FlashVars="'+flashvars+'"><\/embed>\n<\/object>\n';
} else {  
document.getElementById('spFlashvideoMovie'+videoid).innerHTML = '<div style="width:100%;height:149px;padding:3px;background-color:#f0f0f0">\nDer benötigte Flash Player 8 wurde nicht gefunden.<br />\nMögliche Ursachen:<br /><br />\nJavaScript erkennt den Player nicht korrekt.<br />\n		<br />\nDer Player ist nicht vorhanden.<br />\n<a href="http://www.macromedia.com/go/getflash/" target="_blank">Jetzt installieren<\/a>\n<\/div>';
spShowHiddenFlashplayerDiv(videoid);
document.getElementById('spFlashvideoPicCredit'+videoid).style.display = 'none';
}
}
}
function spShowHiddenFlashplayerDiv(videoid){
document.getElementById('spFlashvideoMovie'+videoid).style.zIndex=3;
document.getElementById('spFlashvideoPicCredit'+videoid).style.display = 'none';
document.getElementById('spFlashvideoVideoCredit'+videoid).style.display = 'block';
}
function spOpenFlashvideoPopup(videoid){
window.open('http://www.spiegel.de/videoplayer/0,6298,'+videoid+',00.html', 'SPONflashvideo', SpOnENV_FlashvideoPopupParams).focus();
}
var spOasTimeseenUrl = '';
var spOasCurrentTimeseen = -1;
function spOasSetCurrentTimeseen(seconds) {
spOasCurrentTimeseen = seconds;
}
function spOasSetTimeseenUrl(url) {
spOasTimeseenUrl = url;
}
function spOasSetReminder(html){
document.getElementById('spOasReminder').innerHTML = html;
}
function spOasTimeseenOnunload() {
if (spOasCurrentTimeseen >= 0 && spOasCurrentTimeseen < 999)
{
if (spOasTimeseenUrl)
{
document.write('<img src="' + spOasTimeseenUrl.replace("\[sekunden.gif\]", spOasCurrentTimeseen + ".gif") + '" border="0" width="1" height="1" />');
OAS_finaltime = (new Date).getTime() + 500;
while ((new Date).getTime() < OAS_finaltime)
{
}
}
}
}
spFlashDetectInit();