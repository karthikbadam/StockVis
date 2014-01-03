//Correlation Chart class

function CorrelationChart(options) {  
    var selectedSymbolsData = this.selectedSymbolsData = options.selectedSymbolsData;
    var selectedSymbols = this.selectedSymbols = options.selectedSymbols; 
    var color = this.color = options.color; 
    
    var nodes = this.nodes = [];    
    var links = this.links = [];
    
    var margin = this.margin = {top: 60, right: 20, bottom: 60, left: 20},
        width = ($("#correlation-viewer").parent().width()/3 - margin.left - margin.right),
        height = ($("#correlation-viewer").parent().height() - margin.top - margin.bottom);
        
    var force = this.force = d3.layout.force()
    .charge(-120)
    .linkDistance(function(d) { return 10*Math.sqrt(d.value);} )
    .size([width, height]);

    this.width = width;
    this.height = height;
   
    this.div = d3.select("#correlation-viewer");    
}

CorrelationChart.prototype.refresh = function () {
    var margin = this.margin;
    
    var selectedSymbolsData = this.selectedSymbolsData;
    var selectedSymbols = this.selectedSymbols;
    
    this.div = d3.select("#correlation-viewer");
       
    var svg = this.svg = this.div.append("svg")
        .attr("class", "correlation-svg")
        .attr("width", this.width + margin.left + margin.right )
        .attr("height", this.height + margin.top + margin.bottom )
        .append("g")
        .attr("transform", "translate(" + margin.left + "," + margin.top + ")");

    var nodes = this.nodes = [];    
    var links = this.links = [];

    for (var i = 0; i < selectedSymbols.length; i++) {
        var node = {};
        node.name = selectedSymbols[i];
        node.id = i;
        nodes.push(node);
    }
    
    for (var i = 0; i < selectedSymbols.length; i++) {
        for (var j = i+1; j < selectedSymbols.length; j++ ) {
            var link1 = {};
            link1.source = i;
            link1.target = j;
            var data1 = this.selectedSymbolsData[i];
            var data2 = this.selectedSymbolsData[j];
            var value1 = 100*(data1[0]['Adj Close'] - data1[1]['Adj Close'])/data1[1]['Adj Close'];
            var value2 = 100*(data2[0]['Adj Close'] - data2[1]['Adj Close'])/data2[1]['Adj Close'];
            link1.value = (value1 - value2)*(value1 - value2);
            console.log(link1);
            this.links.push(link1);
        }
    }
    
    console.log(links);
    var color = this.color;
    
    var force = this.force;
    
    force
      .nodes(nodes)
      .links(links)
      .start();

    var link = svg.selectAll(".link")
        .data(links)
        .enter().append("line")
        .attr("class", "link")
        .attr("stroke", "#aaa")
        .attr("stroke-width", "0.5px");

    var node = svg.selectAll(".node")
        .data(nodes)
      .enter().append("circle")
        .attr("class", "node")
        .attr("r", 7)
        .style("fill", function(d) { return color(d.id); })
        .call(force.drag);

    node.append("title")
        .text(function(d) { return d.name; });

    force.on("tick", function() {
      link.attr("x1", function(d) { return d.source.x; })
          .attr("y1", function(d) { return d.source.y; })
          .attr("x2", function(d) { return d.target.x; })
          .attr("y2", function(d) { return d.target.y; });

      node.attr("cx", function(d) { return d.x; })
          .attr("cy", function(d) { return d.y; });
    });
    
};


