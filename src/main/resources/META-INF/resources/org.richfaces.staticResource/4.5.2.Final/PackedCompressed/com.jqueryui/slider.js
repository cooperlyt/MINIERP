/*
 * jQuery UI Slider 1.11.2
 * http://jqueryui.com
 *
 * Copyright 2014 jQuery Foundation and other contributors
 * Released under the MIT license.
 * http://jquery.org/license
 *
 * http://api.jqueryui.com/slider/
 */
(function(A){if(typeof define==="function"&&define.amd){define(["jquery","./core","./mouse","./widget"],A)
}else{A(jQuery)
}}(function(A){return A.widget("ui.slider",A.ui.mouse,{version:"1.11.2",widgetEventPrefix:"slide",options:{animate:false,distance:0,max:100,min:0,orientation:"horizontal",range:false,step:1,value:0,values:null,change:null,slide:null,start:null,stop:null},numPages:5,_create:function(){this._keySliding=false;
this._mouseSliding=false;
this._animateOff=true;
this._handleIndex=null;
this._detectOrientation();
this._mouseInit();
this._calculateNewMax();
this.element.addClass("ui-slider ui-slider-"+this.orientation+" ui-widget ui-widget-content ui-corner-all");
this._refresh();
this._setOption("disabled",this.options.disabled);
this._animateOff=false
},_refresh:function(){this._createRange();
this._createHandles();
this._setupEvents();
this._refreshValue()
},_createHandles:function(){var E,B,C=this.options,G=this.element.find(".ui-slider-handle").addClass("ui-state-default ui-corner-all"),F="<span class='ui-slider-handle ui-state-default ui-corner-all' tabindex='0'></span>",D=[];
B=(C.values&&C.values.length)||1;
if(G.length>B){G.slice(B).remove();
G=G.slice(0,B)
}for(E=G.length;
E<B;
E++){D.push(F)
}this.handles=G.add(A(D.join("")).appendTo(this.element));
this.handle=this.handles.eq(0);
this.handles.each(function(H){A(this).data("ui-slider-handle-index",H)
})
},_createRange:function(){var B=this.options,C="";
if(B.range){if(B.range===true){if(!B.values){B.values=[this._valueMin(),this._valueMin()]
}else{if(B.values.length&&B.values.length!==2){B.values=[B.values[0],B.values[0]]
}else{if(A.isArray(B.values)){B.values=B.values.slice(0)
}}}}if(!this.range||!this.range.length){this.range=A("<div></div>").appendTo(this.element);
C="ui-slider-range ui-widget-header ui-corner-all"
}else{this.range.removeClass("ui-slider-range-min ui-slider-range-max").css({left:"",bottom:""})
}this.range.addClass(C+((B.range==="min"||B.range==="max")?" ui-slider-range-"+B.range:""))
}else{if(this.range){this.range.remove()
}this.range=null
}},_setupEvents:function(){this._off(this.handles);
this._on(this.handles,this._handleEvents);
this._hoverable(this.handles);
this._focusable(this.handles)
},_destroy:function(){this.handles.remove();
if(this.range){this.range.remove()
}this.element.removeClass("ui-slider ui-slider-horizontal ui-slider-vertical ui-widget ui-widget-content ui-corner-all");
this._mouseDestroy()
},_mouseCapture:function(D){var H,K,C,F,J,L,G,B,I=this,E=this.options;
if(E.disabled){return false
}this.elementSize={width:this.element.outerWidth(),height:this.element.outerHeight()};
this.elementOffset=this.element.offset();
H={x:D.pageX,y:D.pageY};
K=this._normValueFromMouse(H);
C=this._valueMax()-this._valueMin()+1;
this.handles.each(function(M){var N=Math.abs(K-I.values(M));
if((C>N)||(C===N&&(M===I._lastChangedValue||I.values(M)===E.min))){C=N;
F=A(this);
J=M
}});
L=this._start(D,J);
if(L===false){return false
}this._mouseSliding=true;
this._handleIndex=J;
F.addClass("ui-state-active").focus();
G=F.offset();
B=!A(D.target).parents().addBack().is(".ui-slider-handle");
this._clickOffset=B?{left:0,top:0}:{left:D.pageX-G.left-(F.width()/2),top:D.pageY-G.top-(F.height()/2)-(parseInt(F.css("borderTopWidth"),10)||0)-(parseInt(F.css("borderBottomWidth"),10)||0)+(parseInt(F.css("marginTop"),10)||0)};
if(!this.handles.hasClass("ui-state-hover")){this._slide(D,J,K)
}this._animateOff=true;
return true
},_mouseStart:function(){return true
},_mouseDrag:function(D){var B={x:D.pageX,y:D.pageY},C=this._normValueFromMouse(B);
this._slide(D,this._handleIndex,C);
return false
},_mouseStop:function(B){this.handles.removeClass("ui-state-active");
this._mouseSliding=false;
this._stop(B,this._handleIndex);
this._change(B,this._handleIndex);
this._handleIndex=null;
this._clickOffset=null;
this._animateOff=false;
return false
},_detectOrientation:function(){this.orientation=(this.options.orientation==="vertical")?"vertical":"horizontal"
},_normValueFromMouse:function(C){var B,F,E,D,G;
if(this.orientation==="horizontal"){B=this.elementSize.width;
F=C.x-this.elementOffset.left-(this._clickOffset?this._clickOffset.left:0)
}else{B=this.elementSize.height;
F=C.y-this.elementOffset.top-(this._clickOffset?this._clickOffset.top:0)
}E=(F/B);
if(E>1){E=1
}if(E<0){E=0
}if(this.orientation==="vertical"){E=1-E
}D=this._valueMax()-this._valueMin();
G=this._valueMin()+E*D;
return this._trimAlignValue(G)
},_start:function(D,C){var B={handle:this.handles[C],value:this.value()};
if(this.options.values&&this.options.values.length){B.value=this.values(C);
B.values=this.values()
}return this._trigger("start",D,B)
},_slide:function(F,E,D){var B,C,G;
if(this.options.values&&this.options.values.length){B=this.values(E?0:1);
if((this.options.values.length===2&&this.options.range===true)&&((E===0&&D>B)||(E===1&&D<B))){D=B
}if(D!==this.values(E)){C=this.values();
C[E]=D;
G=this._trigger("slide",F,{handle:this.handles[E],value:D,values:C});
B=this.values(E?0:1);
if(G!==false){this.values(E,D)
}}}else{if(D!==this.value()){G=this._trigger("slide",F,{handle:this.handles[E],value:D});
if(G!==false){this.value(D)
}}}},_stop:function(D,C){var B={handle:this.handles[C],value:this.value()};
if(this.options.values&&this.options.values.length){B.value=this.values(C);
B.values=this.values()
}this._trigger("stop",D,B)
},_change:function(D,C){if(!this._keySliding&&!this._mouseSliding){var B={handle:this.handles[C],value:this.value()};
if(this.options.values&&this.options.values.length){B.value=this.values(C);
B.values=this.values()
}this._lastChangedValue=C;
this._trigger("change",D,B)
}},value:function(B){if(arguments.length){this.options.value=this._trimAlignValue(B);
this._refreshValue();
this._change(null,0);
return 
}return this._value()
},values:function(C,F){var E,B,D;
if(arguments.length>1){this.options.values[C]=this._trimAlignValue(F);
this._refreshValue();
this._change(null,C);
return 
}if(arguments.length){if(A.isArray(arguments[0])){E=this.options.values;
B=arguments[0];
for(D=0;
D<E.length;
D+=1){E[D]=this._trimAlignValue(B[D]);
this._change(null,D)
}this._refreshValue()
}else{if(this.options.values&&this.options.values.length){return this._values(C)
}else{return this.value()
}}}else{return this._values()
}},_setOption:function(C,D){var B,E=0;
if(C==="range"&&this.options.range===true){if(D==="min"){this.options.value=this._values(0);
this.options.values=null
}else{if(D==="max"){this.options.value=this._values(this.options.values.length-1);
this.options.values=null
}}}if(A.isArray(this.options.values)){E=this.options.values.length
}if(C==="disabled"){this.element.toggleClass("ui-state-disabled",!!D)
}this._super(C,D);
switch(C){case"orientation":this._detectOrientation();
this.element.removeClass("ui-slider-horizontal ui-slider-vertical").addClass("ui-slider-"+this.orientation);
this._refreshValue();
this.handles.css(D==="horizontal"?"bottom":"left","");
break;
case"value":this._animateOff=true;
this._refreshValue();
this._change(null,0);
this._animateOff=false;
break;
case"values":this._animateOff=true;
this._refreshValue();
for(B=0;
B<E;
B+=1){this._change(null,B)
}this._animateOff=false;
break;
case"step":case"min":case"max":this._animateOff=true;
this._calculateNewMax();
this._refreshValue();
this._animateOff=false;
break;
case"range":this._animateOff=true;
this._refresh();
this._animateOff=false;
break
}},_value:function(){var B=this.options.value;
B=this._trimAlignValue(B);
return B
},_values:function(B){var E,D,C;
if(arguments.length){E=this.options.values[B];
E=this._trimAlignValue(E);
return E
}else{if(this.options.values&&this.options.values.length){D=this.options.values.slice();
for(C=0;
C<D.length;
C+=1){D[C]=this._trimAlignValue(D[C])
}return D
}else{return[]
}}},_trimAlignValue:function(E){if(E<=this._valueMin()){return this._valueMin()
}if(E>=this._valueMax()){return this._valueMax()
}var B=(this.options.step>0)?this.options.step:1,D=(E-this._valueMin())%B,C=E-D;
if(Math.abs(D)*2>=B){C+=(D>0)?B:(-B)
}return parseFloat(C.toFixed(5))
},_calculateNewMax:function(){var B=(this.options.max-this._valueMin())%this.options.step;
this.max=this.options.max-B
},_valueMin:function(){return this.options.min
},_valueMax:function(){return this.max
},_refreshValue:function(){var G,F,J,H,K,E=this.options.range,D=this.options,I=this,C=(!this._animateOff)?D.animate:false,B={};
if(this.options.values&&this.options.values.length){this.handles.each(function(L){F=(I.values(L)-I._valueMin())/(I._valueMax()-I._valueMin())*100;
B[I.orientation==="horizontal"?"left":"bottom"]=F+"%";
A(this).stop(1,1)[C?"animate":"css"](B,D.animate);
if(I.options.range===true){if(I.orientation==="horizontal"){if(L===0){I.range.stop(1,1)[C?"animate":"css"]({left:F+"%"},D.animate)
}if(L===1){I.range[C?"animate":"css"]({width:(F-G)+"%"},{queue:false,duration:D.animate})
}}else{if(L===0){I.range.stop(1,1)[C?"animate":"css"]({bottom:(F)+"%"},D.animate)
}if(L===1){I.range[C?"animate":"css"]({height:(F-G)+"%"},{queue:false,duration:D.animate})
}}}G=F
})
}else{J=this.value();
H=this._valueMin();
K=this._valueMax();
F=(K!==H)?(J-H)/(K-H)*100:0;
B[this.orientation==="horizontal"?"left":"bottom"]=F+"%";
this.handle.stop(1,1)[C?"animate":"css"](B,D.animate);
if(E==="min"&&this.orientation==="horizontal"){this.range.stop(1,1)[C?"animate":"css"]({width:F+"%"},D.animate)
}if(E==="max"&&this.orientation==="horizontal"){this.range[C?"animate":"css"]({width:(100-F)+"%"},{queue:false,duration:D.animate})
}if(E==="min"&&this.orientation==="vertical"){this.range.stop(1,1)[C?"animate":"css"]({height:F+"%"},D.animate)
}if(E==="max"&&this.orientation==="vertical"){this.range[C?"animate":"css"]({height:(100-F)+"%"},{queue:false,duration:D.animate})
}}},_handleEvents:{keydown:function(F){var G,D,C,E,B=A(F.target).data("ui-slider-handle-index");
switch(F.keyCode){case A.ui.keyCode.HOME:case A.ui.keyCode.END:case A.ui.keyCode.PAGE_UP:case A.ui.keyCode.PAGE_DOWN:case A.ui.keyCode.UP:case A.ui.keyCode.RIGHT:case A.ui.keyCode.DOWN:case A.ui.keyCode.LEFT:F.preventDefault();
if(!this._keySliding){this._keySliding=true;
A(F.target).addClass("ui-state-active");
G=this._start(F,B);
if(G===false){return 
}}break
}E=this.options.step;
if(this.options.values&&this.options.values.length){D=C=this.values(B)
}else{D=C=this.value()
}switch(F.keyCode){case A.ui.keyCode.HOME:C=this._valueMin();
break;
case A.ui.keyCode.END:C=this._valueMax();
break;
case A.ui.keyCode.PAGE_UP:C=this._trimAlignValue(D+((this._valueMax()-this._valueMin())/this.numPages));
break;
case A.ui.keyCode.PAGE_DOWN:C=this._trimAlignValue(D-((this._valueMax()-this._valueMin())/this.numPages));
break;
case A.ui.keyCode.UP:case A.ui.keyCode.RIGHT:if(D===this._valueMax()){return 
}C=this._trimAlignValue(D+E);
break;
case A.ui.keyCode.DOWN:case A.ui.keyCode.LEFT:if(D===this._valueMin()){return 
}C=this._trimAlignValue(D-E);
break
}this._slide(F,B,C)
},keyup:function(C){var B=A(C.target).data("ui-slider-handle-index");
if(this._keySliding){this._keySliding=false;
this._stop(C,B);
this._change(C,B);
A(C.target).removeClass("ui-state-active")
}}}})
}));