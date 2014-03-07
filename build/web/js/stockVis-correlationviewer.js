//Correlation Chart class

function CorrelationChart(options) { 
    var _self = this; 
    
    _self.selectedSymbolsData = options.selectedSymbolsData;
    _self.selectedSymbols = options.selectedSymbols; 
    _self.color = options.color; 
    
    _self.nodes = [];    
    _self.links = [];
    
    _self.margin = {top: 60, right: 20, bottom: 60, left: 20};
    _self.width = ($("#correlation-viewer").parent().width()/3 - _self.margin.left - _self.margin.right - 5);
    _self.height = ($("#correlation-viewer").parent().height() - _self.margin.top - _self.margin.bottom);
        
    _self.force = d3.layout.force()
    .charge(-120)
    .linkDistance(function(d) { return 10*Math.sqrt(d.value);} )
    .size([_self.width, _self.height]);

    _self.div = d3.select("#correlation-viewer");    
}

CorrelationChart.prototype.refresh = function () {
    var _self = this; 
    
    _self.div = d3.select("#correlation-viewer");
       
    _self.svg = _self.div.append("svg")
        .attr("class", "correlation-svg")
        .attr("width", _self.width + _self.margin.left + _self.margin.right )
        .attr("height", _self.height + _self.margin.top + _self.margin.bottom )
        .append("g")
        .attr("transform", "translate(" + _self.margin.left + "," + _self.margin.top + ")");

    _self.nodes = [];    
    _self.links = [];

    for (var i = 0; i < _self.selectedSymbols.length; i++) {
        var node1 = {};
        node1.name = _self.selectedSymbols[i];
        node1.id = i;
        _self.nodes.push(node1);
    }
    
    for (var i = 0; i < _self.selectedSymbols.length; i++) {
        for (var j = i+1; j < _self.selectedSymbols.length; j++ ) {
            var link1 = {};
            link1.source = i;
            link1.target = j;
            var data1 = _self.selectedSymbolsData[i];
            var data2 = _self.selectedSymbolsData[j];
            var value1 = 100*(data1[0][stockColumns[6]] - data1[1][stockColumns[6]])/data1[1][stockColumns[6]];
            var value2 = 100*(data2[0][stockColumns[6]] - data2[1][stockColumns[6]])/data2[1][stockColumns[6]];
            link1.value = Math.pow(value1 - value2, 2);
            _self.links.push(link1);
        }
    }
    
    _self.force.nodes(_self.nodes)
      .links(_self.links)
      .start();

    _self.link = _self.svg.selectAll(".link")
        .data(_self.links)
        .enter().append("line")
        .attr("class", "link")
        .attr("stroke", "#aaa")
        .attr("stroke-width", "0.5px");

    _self.node = _self.svg.selectAll(".node")
        .data(_self.nodes)
        .enter().append("circle")
        .attr("class", "node")
        .attr("r", 7)
        .style("fill", function(d) { return color(d.id); })
        .call(_self.force.drag);

    _self.node.append("title")
        .text(function(d) { return d.name; });

    _self.force.on("tick", function() {
      _self.link.attr("x1", function(d) { return d.source.x; })
          .attr("y1", function(d) { return d.source.y; })
          .attr("x2", function(d) { return d.target.x; })
          .attr("y2", function(d) { return d.target.y; });

      _self.node.attr("cx", function(d) { return d.x; })
          .attr("cy", function(d) { return d.y; });
    });
    
};


