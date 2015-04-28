(function(D,T){T.ui=T.ui||{};
T.ui.Autocomplete=function(c,a,b){this.namespace="."+T.Event.createNamespace(this.name,c);
this.options={};
Y.constructor.call(this,c,c+K.SELECT,a,b);
this.attachToDom();
this.options=D.extend(this.options,X,b);
this.value="";
this.index=null;
this.isFirstAjax=true;
Q.call(this);
P.call(this);
M.call(this,"")
};
T.ui.AutocompleteBase.extend(T.ui.Autocomplete);
var Y=T.ui.Autocomplete.$super;
var X={itemClass:"rf-au-itm",selectedItemClass:"rf-au-itm-sel",subItemClass:"rf-au-opt",selectedSubItemClass:"rf-au-opt-sel",autofill:true,minChars:1,selectFirst:true,ajaxMode:true,lazyClientMode:false,isCachedAjax:true,tokens:"",attachToBody:true,filterFunction:undefined};
var K={SELECT:"List",ITEMS:"Items",VALUE:"Value"};
var A=/^[\n\s]*(.*)[\n\s]*$/;
var O=function(a){var b=[];
a.each(function(){b.push(D(this).text().replace(A,"$1"))
});
return b
};
var Q=function(){this.useTokens=(typeof this.options.tokens=="string"&&this.options.tokens.length>0);
if(this.useTokens){var a=this.options.tokens.split("").join("\\");
this.REGEXP_TOKEN_RIGHT=new RegExp("["+a+"]","i");
this.getLastTokenIndex=function(b){return RichFaces.ui.Autocomplete.__getLastTokenIndex(a,b)
}
}};
var P=function(){var a=D(T.getDomElement(this.id+K.ITEMS).parentNode);
a.on("click"+this.namespace,"."+this.options.itemClass,D.proxy(G,this));
a.on("mouseenter"+this.namespace,"."+this.options.itemClass,D.proxy(N,this))
};
var N=function(c){var b=D(c.target);
if(b&&!b.hasClass(this.options.itemClass)){b=b.parents("."+this.options.itemClass).get(0)
}if(b){var a=this.items.index(b);
F.call(this,c,a)
}};
var G=function(b){var a=D(b.target);
if(a&&!a.hasClass(this.options.itemClass)){a=a.parents("."+this.options.itemClass).get(0)
}if(a){this.__onEnter(b);
T.Selection.setCaretTo(T.getDomElement(this.fieldId));
this.__hide(b)
}};
var M=function(c,b){var d=D(T.getDomElement(this.id+K.ITEMS));
this.items=d.find("."+this.options.itemClass);
var a=d.data("componentData");
d.removeData("componentData");
if(this.items.length>0){this.cache=new T.utils.Cache((this.options.ajaxMode?c:""),this.items,b||a||O,!this.options.ajaxMode)
}};
var E=function(){var b=0;
this.items.slice(0,this.index).each(function(){b+=this.offsetHeight
});
var a=D(T.getDomElement(this.id+K.ITEMS)).parent();
if(b<a.scrollTop()){a.scrollTop(b)
}else{b+=this.items.eq(this.index).outerHeight();
if(b-a.scrollTop()>a.innerHeight()){a.scrollTop(b-a.innerHeight())
}}};
var R=function(a,c){if(this.options.autofill&&c.toLowerCase().indexOf(a)==0){var d=T.getDomElement(this.fieldId);
var e=T.Selection.getStart(d);
this.__setInputValue(a+c.substring(a.length));
var b=e+c.length-a.length;
T.Selection.set(d,e,b)
}};
var J=function(d,g){T.getDomElement(this.id+K.VALUE).value=this.value;
var f=this;
var a=d;
var c=function(h){M.call(f,f.value,h.componentData&&h.componentData[f.id]);
if(f.options.lazyClientMode&&f.value.length!=0){I.call(f,f.value)
}if(f.items.length!=0){if(g){(f.focused||f.isMouseDown)&&g.call(f,a)
}else{f.isVisible&&f.options.selectFirst&&F.call(f,a,0)
}}else{f.__hide(a)
}};
var b=function(h){f.__hide(a);
Z.call(f)
};
this.isFirstAjax=false;
var e={};
e[this.id+".ajax"]="1";
T.ajax(this.id,d,{parameters:e,error:b,complete:c})
};
var V=function(){if(this.index!=null){var a=this.items.eq(this.index);
if(a.removeClass(this.options.selectedItemClass).hasClass(this.options.subItemClass)){a.removeClass(this.options.selectedSubItemClass)
}this.index=null
}};
var F=function(d,a,c){if(this.items.length==0||(!c&&a==this.index)){return 
}if(a==null||a==undefined){V.call(this);
return 
}if(c){if(this.index==null){a=0
}else{a=this.index+a
}}if(a<0){a=0
}else{if(a>=this.items.length){a=this.items.length-1
}}if(a==this.index){return 
}V.call(this);
this.index=a;
var b=this.items.eq(this.index);
if(b.addClass(this.options.selectedItemClass).hasClass(this.options.subItemClass)){b.addClass(this.options.selectedSubItemClass)
}E.call(this);
if(d&&d.keyCode!=T.KEYS.BACKSPACE&&d.keyCode!=T.KEYS.DEL&&d.keyCode!=T.KEYS.LEFT&&d.keyCode!=T.KEYS.RIGHT){R.call(this,this.value,S.call(this))
}};
var I=function(b){var a=this.cache.getItems(b,this.options.filterFunction);
this.items=D(a);
D(T.getDomElement(this.id+K.ITEMS)).empty().append(this.items)
};
var Z=function(){D(T.getDomElement(this.id+K.ITEMS)).removeData().empty();
this.items=[]
};
var C=function(b,d,e){F.call(this,b);
var c=(typeof d=="undefined")?this.__getSubValue():d;
var a=this.value;
this.value=c;
if((this.options.isCachedAjax||!this.options.ajaxMode)&&this.cache&&this.cache.isCached(c)){if(a!=c){I.call(this,c)
}if(this.items.length!=0){e&&e.call(this,b)
}else{this.__hide(b)
}if(b.keyCode==T.KEYS.RETURN||b.type=="click"){this.__setInputValue(c)
}else{if(this.options.selectFirst){F.call(this,b,0)
}}}else{if(b.keyCode==T.KEYS.RETURN||b.type=="click"){this.__setInputValue(c)
}if(c.length>=this.options.minChars){if((this.options.ajaxMode||this.options.lazyClientMode)&&(a!=c||(a===""&&c===""))){J.call(this,b,e)
}}else{if(this.options.ajaxMode){Z.call(this);
this.__hide(b)
}}}};
var S=function(){if(this.index!=null){var a=this.items.eq(this.index);
return this.cache.getItemValue(a)
}return undefined
};
var W=function(){if(this.useTokens){var f=T.getDomElement(this.fieldId);
var e=f.value;
var b=T.Selection.getStart(f);
var c=e.substring(0,b);
var d=e.substring(b);
var a=c.substring(this.getLastTokenIndex(c));
r=d.search(this.REGEXP_TOKEN_RIGHT);
if(r==-1){r=d.length
}a+=d.substring(0,r);
return a
}else{return this.getValue()
}};
var H=function(a){var b=T.Selection.getStart(a);
if(b<=0){b=this.getLastTokenIndex(a.value)
}return b
};
var L=function(j){var i=T.getDomElement(this.fieldId);
var c=i.value;
var a=this.__getCursorPosition(i);
var e=c.substring(0,a);
var g=c.substring(a);
var h=this.getLastTokenIndex(e);
var f=h!=-1?h:e.length;
h=g.search(this.REGEXP_TOKEN_RIGHT);
var b=h!=-1?h:g.length;
var d=c.substring(0,f)+j;
a=d.length;
i.value=d+g.substring(b);
i.focus();
T.Selection.setCaretTo(i,a);
return i.value
};
var B=function(){if(this.items.length==0){return -1
}var d=D(T.getDomElement(this.id+K.ITEMS)).parent();
var b=d.scrollTop()+d.innerHeight()+this.items[0].offsetTop;
var c;
var a=(this.index!=null&&this.items[this.index].offsetTop<=b)?this.index:0;
for(a;
a<this.items.length;
a++){c=this.items[a];
if(c.offsetTop+c.offsetHeight>b){a--;
break
}}if(a!=this.items.length-1&&a==this.index){b+=this.items[a].offsetTop-d.scrollTop();
for(++a;
a<this.items.length;
a++){c=this.items[a];
if(c.offsetTop+c.offsetHeight>b){break
}}}return a
};
var U=function(){if(this.items.length==0){return -1
}var d=D(T.getDomElement(this.id+K.ITEMS)).parent();
var b=d.scrollTop()+this.items[0].offsetTop;
var c;
var a=(this.index!=null&&this.items[this.index].offsetTop>=b)?this.index-1:this.items.length-1;
for(a;
a>=0;
a--){c=this.items[a];
if(c.offsetTop<b){a++;
break
}}if(a!=0&&a==this.index){b=this.items[a].offsetTop-d.innerHeight();
if(b<this.items[0].offsetTop){b=this.items[0].offsetTop
}for(--a;
a>=0;
a--){c=this.items[a];
if(c.offsetTop<b){a++;
break
}}}return a
};
D.extend(T.ui.Autocomplete.prototype,(function(){return{name:"Autocomplete",__updateState:function(a){var b=this.__getSubValue();
if(this.items.length==0&&this.isFirstAjax){if((this.options.ajaxMode&&b.length>=this.options.minChars)||this.options.lazyClientMode){this.value=b;
J.call(this,a,this.__show);
return true
}}return false
},__getSubValue:W,__getCursorPosition:H,__updateInputValue:function(a){if(this.useTokens){return L.call(this,a)
}else{return Y.__updateInputValue.call(this,a)
}},__setInputValue:function(a){this.currentValue=this.__updateInputValue(a)
},__onChangeValue:C,__onKeyUp:function(a){F.call(this,a,-1,true)
},__onKeyDown:function(a){F.call(this,a,1,true)
},__onPageUp:function(a){F.call(this,a,U.call(this))
},__onPageDown:function(a){F.call(this,a,B.call(this))
},__onKeyHome:function(a){F.call(this,a,0)
},__onKeyEnd:function(a){F.call(this,a,this.items.length-1)
},__onBeforeShow:function(a){},__onEnter:function(a){var b=S.call(this);
this.__onChangeValue(a,b);
this.invokeEvent("selectitem",T.getDomElement(this.fieldId),a,b)
},__onShow:function(a){if(this.options.selectFirst){F.call(this,a,0)
}},__onHide:function(a){F.call(this,a)
},destroy:function(){this.items=null;
this.cache=null;
var a=T.getDomElement(this.id+K.ITEMS);
D(a).removeData();
T.Event.unbind(a.parentNode,this.namespace);
this.__conceal();
Y.destroy.call(this)
}}
})());
D.extend(T.ui.Autocomplete,{setData:function(b,a){D(T.getDomElement(b)).data("componentData",a)
},__getLastTokenIndex:function(g,f){var b=new RegExp("["+g+"][^"+g+"]*$","i");
var c=new RegExp("[^"+g+" ]","i");
var f=f||"";
var e=f.search(b);
if(e<0){return 0
}var a=f.substring(e);
var d=a.search(c);
if(d<=0){d=a.length
}return e+d
}})
})(RichFaces.jQuery,RichFaces);