(function(E,D){var B={charttype:"",xtype:"",ytype:"",zoom:false,grid:{clickable:true,hoverable:true},tooltip:true,tooltipOpts:{content:"%s  [%x,%y]",shifts:{x:20,y:0},defaultTheme:false},legend:{postion:"ne",sorted:"ascending"},xaxis:{min:null,max:null,autoscaleMargin:null,axisLabel:""},yaxis:{min:null,max:null,autoscaleMargin:0.2,axisLabel:""},data:[]};
var A={series:{pie:{show:true}},tooltipOpts:{content:" %p.0%, %s"}};
var C=function(H,G){var I={};
I[G+"name"]="plotclick";
I[G+"seriesIndex"]=H.data.seriesIndex;
I[G+"dataIndex"]=H.data.dataIndex;
I[G+"x"]=H.data.x;
I[G+"y"]=H.data.y;
D.ajax(G,H,{parameters:I,incId:1})
};
D.ui=D.ui||{};
D.ui.Chart=D.BaseComponent.extendClass({name:"Chart",init:function(K,J){F.constructor.call(this,K,J);
this.namespace=this.namespace||"."+RichFaces.Event.createNamespace(this.name,this.id);
this.attachToDom();
this.options=E.extend(true,{},B,J);
this.element=E(document.getElementById(K));
this.chartElement=this.element.find(".chart");
if(this.options.charttype==="pie"){this.options=E.extend(true,{},this.options,A);
this.options.data=this.options.data[0]
}else{if(this.options.charttype==="bar"){if(this.options.xtype==="string"){this.options.xaxis.tickLength=0;
var H=this.options.data[0].data.length,M=this.options.data.length,N=[],G=false;
this.options.bars=this.options.bars||{};
this.options.bars.barWidth=1/(M+1);
for(var L=0;
L<H;
L++){N.push([L,this.options.data[0].data[L][0]]);
for(var I=0;
I<M;
I++){this.options.data[I].data[L][0]=L;
if(!G){this.options.data[I].bars.order=I
}}G=true
}this.options.xaxis.ticks=N
}}else{if(J.charttype==="line"){if(this.options.xtype==="string"){this.options.xaxis.tickLength=0;
var H=this.options.data[0].data.length,M=this.options.data.length,N=[];
for(var L=0;
L<H;
L++){N.push([L,this.options.data[0].data[L][0]]);
for(var I=0;
I<M;
I++){this.options.data[I].data[L][0]=L
}}this.options.xaxis.ticks=N
}if(J.zoom){this.options.selection={mode:"xy"}
}if(this.options.xtype==="date"){this.options=E.extend({},this.options,dateDefaults);
if(this.options.xaxis.format){this.options.xaxis.timeformat=this.options.xaxis.format
}}}}}this.plot=E.plot(this.chartElement,this.options.data,this.options);
this.__bindEventHandlers(this.chartElement,this.options)
},getPlotObject:function(){return this.plot
},highlight:function(G,H){this.plot.highlight(G,H)
},unhighlight:function(G,H){this.plot.unhighlight(G,H)
},__bindEventHandlers:function(H,G){this.chartElement.on("plotclick",this._getPlotClickHandler(this.options,this.chartElement,C));
this.chartElement.on("plothover",this._getPlotHoverHandler(this.options,this.chartElement));
if(this.options.handlers&&this.options.handlers.onmouseout){this.chartElement.on("mouseout",this.options.handlers.onmouseout)
}if(this.options.zoom){this.chartElement.on("plotselected",E.proxy(this._zoomFunction,this))
}},_getPlotClickHandler:function(I,J,K){var L=I.handlers.onplotclick;
var H=I.particularSeriesHandlers.onplotclick;
var G=this.element.attr("id");
return function(O,M,N){if(N!==null){O.data={seriesIndex:N.seriesIndex,dataIndex:N.dataIndex,x:N.datapoint[0],y:N.datapoint[1],item:N};
if(I.charttype=="pie"){O.data.x=I.data[N.seriesIndex].label;
O.data.y=N.datapoint[1][0][1]
}else{if(I.charttype=="bar"&&I.xtype=="string"){O.data.x=I.xaxis.ticks[N.dataIndex][1]
}}if(I.serverSideListener){if(K){K(O,G)
}}if(L){L.call(J,O)
}if(H[O.data.seriesIndex]){H[O.data.seriesIndex].call(J,O)
}}}
},_getPlotHoverHandler:function(G,I){var H=G.handlers.onplothover;
var J=G.particularSeriesHandlers.onplothover;
return function(M,K,L){if(L!==null){M.data={seriesIndex:L.seriesIndex,dataIndex:L.dataIndex,x:L.datapoint[0],y:L.datapoint[1],item:L};
if(H){H.call(I,M)
}if(J[M.data.seriesIndex]){J[M.data.seriesIndex].call(I,M)
}}}
},_zoomFunction:function(H,G){var I=this.getPlotObject();
E.each(I.getXAxes(),function(J,K){var L=K.options;
L.min=G.xaxis.from;
L.max=G.xaxis.to
});
I.setupGrid();
I.draw();
I.clearSelection()
},resetZoom:function(){this.plot=E.plot(this.chartElement,this.options.data,this.options)
},destroy:function(){D.Event.unbindById(this.id,"."+this.namespace);
F.destroy.call(this)
}});
var F=D.ui.Chart.$super
})(RichFaces.jQuery,RichFaces);