function com_google_gwt_sample_ajaxfeed_AjaxFeed(){var k=window,j=document,s=k.external,D,u,p,o='',w={},ab=[],C=[],n=[],z,B;if(!k.__gwt_stylesLoaded){k.__gwt_stylesLoaded={};}if(!k.__gwt_scriptsLoaded){k.__gwt_scriptsLoaded={};}function t(){try{return s&&(s.gwtOnLoad&&k.location.search.indexOf('gwt.hybrid')== -1);}catch(a){return false;}}
function v(){if(D&&(u&&p)){var c=j.getElementById('com.google.gwt.sample.ajaxfeed.AjaxFeed');var b=c.contentWindow;b.__gwt_initHandlers=com_google_gwt_sample_ajaxfeed_AjaxFeed.__gwt_initHandlers;if(t()){b.__gwt_getProperty=function(a){return q(a);};}com_google_gwt_sample_ajaxfeed_AjaxFeed=null;b.gwtOnLoad(z,'com.google.gwt.sample.ajaxfeed.AjaxFeed',o);}}
function r(){var g,f=j.getElementById('__gwt_js_marker_com.google.gwt.sample.ajaxfeed.AjaxFeed');if(f){g=f.nextSibling;}else{j.write('<script id="__gwt_marker_com.google.gwt.sample.ajaxfeed.AjaxFeed"><\/script>');f=j.getElementById('__gwt_marker_com.google.gwt.sample.ajaxfeed.AjaxFeed');if(f){g=f.previousSibling;}}function d(b){var a=b.lastIndexOf('/');return a>=0?b.substring(0,a+1):'';}
;if(g&&g.src){o=d(g.src);}if(o==''){var c=j.getElementsByTagName('base');if(c.length>0){o=c[c.length-1].href;}else{o=d(j.location.href);}}else if(o.match(/^\w+:\/\//)){}else{var e=j.createElement('img');e.src=o+'clear.cache.gif';o=d(e.src);}if(f){f.parentNode.removeChild(f);}}
function A(){var f=document.getElementsByTagName('meta');for(var d=0,g=f.length;d<g;++d){var e=f[d],h=e.getAttribute('name'),b;if(h){if(h=='gwt:property'){b=e.getAttribute('content');if(b){var i,c=b.indexOf('=');if(c>=0){h=b.substring(0,c);i=b.substring(c+1);}else{h=b;i='';}w[h]=i;}}else if(h=='gwt:onPropertyErrorFn'){b=e.getAttribute('content');if(b){try{B=eval(b);}catch(a){alert('Bad handler "'+b+'" for "gwt:onPropertyErrorFn"');}}}else if(h=='gwt:onLoadErrorFn'){b=e.getAttribute('content');if(b){try{z=eval(b);}catch(a){alert('Bad handler "'+b+'" for "gwt:onLoadErrorFn"');}}}}}}
function m(a,b){return b in ab[a];}
function l(a){var b=w[a];return b==null?null:b;}
function F(d,e){var a=n;for(var b=0,c=d.length-1;b<c;++b){a=a[d[b]]||(a[d[b]]=[]);}a[d[c]]=e;}
function q(d){var e=C[d](),b=ab[d];if(e in b){return e;}var a=[];for(var c in b){a[b[c]]=c;}if(B){B(d,a,e);}throw null;}
C['iPhone']=function(){return navigator.platform=='iPhone'?'true':'false';};ab['iPhone']={'false':0,'true':1};C['user.agent']=function(){var d=navigator.userAgent.toLowerCase();var b=function(a){return parseInt(a[1])*1000+parseInt(a[2]);};if(d.indexOf('opera')!= -1){return 'opera';}else if(d.indexOf('webkit')!= -1){return 'safari';}else if(d.indexOf('msie')!= -1){var c=/msie ([0-9]+)\.([0-9]+)/.exec(d);if(c&&c.length==3){if(b(c)>=6000){return 'ie6';}}}else if(d.indexOf('gecko')!= -1){var c=/rv:([0-9]+)\.([0-9]+)/.exec(d);if(c&&c.length==3){if(b(c)>=1008)return 'gecko1_8';}return 'gecko';}return 'unknown';};ab['user.agent']={'gecko':0,'gecko1_8':1,'ie6':2,'opera':3,'safari':4};com_google_gwt_sample_ajaxfeed_AjaxFeed.onInjectionDone=function(){D=true;v();};com_google_gwt_sample_ajaxfeed_AjaxFeed.onScriptLoad=function(){u=true;v();};r();A();var y;function x(){if(!p){p=true;v();if(j.removeEventListener){j.removeEventListener('DOMContentLoaded',x,false);}if(y){clearInterval(y);}}}
if(j.addEventListener){j.addEventListener('DOMContentLoaded',x,false);}var y=setInterval(function(){if(/loaded|complete/.test(j.readyState)){x();}},50);var E;if(t()){E='hosted.html?com_google_gwt_sample_ajaxfeed_AjaxFeed';}else{try{F(['true','ie6'],'603B476C0FC602DF50F7BF3F54ED5971');F(['false','ie6'],'603B476C0FC602DF50F7BF3F54ED5971');F(['false','safari'],'7521AFC5BCD960507CB0EBB95992B228');F(['true','gecko'],'83A79C95881BD62B564AE2D6C13663CD');F(['false','gecko'],'83A79C95881BD62B564AE2D6C13663CD');F(['false','gecko1_8'],'8A477DBDC55FB3D87DA06858CCDA0F02');F(['true','gecko1_8'],'8A477DBDC55FB3D87DA06858CCDA0F02');F(['true','opera'],'E7C475A38B9FEE2C76C7B26A56B2BDD7');F(['false','opera'],'E7C475A38B9FEE2C76C7B26A56B2BDD7');F(['true','safari'],'FFB1D9FF1A8C0B7D03F9E8BFC27DB566');E=n[q('iPhone')][q('user.agent')];}catch(a){return;}E+='.cache.html';}j.write('<iframe id="com.google.gwt.sample.ajaxfeed.AjaxFeed" style="width:0;height:0;border:0" src="'+o+E+'"><\/iframe>');if(!__gwt_stylesLoaded['AjaxFeed.css']){__gwt_stylesLoaded['AjaxFeed.css']=true;document.write('<link rel="stylesheet" href="'+o+'AjaxFeed.css">');}j.write("<script>com_google_gwt_sample_ajaxfeed_AjaxFeed.onInjectionDone('com.google.gwt.sample.ajaxfeed.AjaxFeed')<\/script>");}
com_google_gwt_sample_ajaxfeed_AjaxFeed.__gwt_initHandlers=function(i,e,j){var d=window,g=d.onresize,f=d.onbeforeunload,h=d.onunload;d.onresize=function(a){i();if(g)g(a);};d.onbeforeunload=function(a){var c=e();var b;if(f)b=f(a);if(c!==null)return c;return b;};d.onunload=function(a){j();if(h)h(a);};};com_google_gwt_sample_ajaxfeed_AjaxFeed();