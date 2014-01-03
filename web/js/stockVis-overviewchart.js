//Overview Chart class

function OverviewChart(options) {

    var brush = this.brush = [];
    var stockObject = this.stockObject = options.stockObject;
    var data = this.data = stockObject.data;
    var stockColumns = this.stockColumns = options.columns;
    var margin = {
        top: 10,
        right: 30,
        bottom: 20,
        left: 30
    };
    
    var color = this.color = options.color;
    var linecharts = this.linecharts = options.linecharts;
    
    var width = this.width = (2*$("#overviewchart-viz").parent().width()/3 - margin.left - margin.right),
        height = this.height = ($("#overviewchart-viz").parent().height() - margin.top - margin.bottom);

    var svg = this.svg = d3.select("#overviewchart-viz").append("svg").attr("class", "overviewchart")
        .attr("width", this.width + margin.left + margin.right)
        .attr("height", this.height + margin.top + margin.bottom)
        .append("g")
        .attr("transform", "translate(" + margin.left + "," + margin.top + ")");

    //Axis x - date -- Axis y - value
    var x = this.x = d3.time.scale()
        .range([0, this.width]);

    var y = this.y = d3.scale.linear()
        .range([this.height - 20, 0]);

    
    x.domain(d3.extent(data, function(stock) {
        return stock[stockColumns[0]];
    }));

    y.domain([0, 1]);

    //x and y axis
    var xAxis = this.xAxis = d3.svg.axis()
        .scale(this.x)
        .orient("bottom");
    //.tickFormat(function(d) { return d3.time.format('%b')(new Date(d)); });

    var yAxis = this.yAxis = d3.svg.axis()
        .scale(this.y)
        .orient("left");

    var line = this.line = d3.svg.line()
        .interpolate("monotone")
        .x(function(d) {
            return x(d[stockColumns[0]]);
        })
        .y(function(d) {
            return y(d.normalized);
        });

    this.svg.append("g")
        .attr("class", "x axis")
        .attr("transform", "translate(0," + this.height + ")")
        .call(this.xAxis);


    this.svg.append("defs")
        .append("clipPath").attr("id", "clip")
        .append("rect")
        .attr("width", this.width).attr("height", this.height);

    this.chartContainer = this.svg.append("g")
        .attr("width", this.width).attr("height", this.height);


    var brush = d3.svg.brush().x(this.x).on("brush", onBrush);
    var context = svg.append("g").attr("class", "context")
        .attr("transform", "translate(" + 0 + "," + (0) + ")");


    this.b = x.domain();

    context.append("g").attr("class", "brush")
        .call(brush).selectAll("rect").attr("y", 0)
        .attr("height", this.height)
        .attr("z-index", 3);


    var _self = this;

    function onBrush() {
        /* this will return a date range to pass into the chart object */
        var b = _self.b = brush.empty() ? x.domain() : brush.extent();
        var empty = brush.empty() ? 1 : 0;
        for (var i = 0; i < linecharts.length; i++) {
            try {
                linecharts[i].showOnly(b, empty);
            } catch (err) {
                //console.log("error caught -" + err);
            }
        }
    }

    //user study part!
    $('#next_button').on('click', function(e) {
        var b = _self.b;
        if (b === x.domain()) {
            return;
        } else {
            var leftDay = b[0];
            var rightDay = b[1];

            leftDay.setDate(b[0].getDate() + 1);
            if (b[0].getDay() === 6) {
                leftDay.setDate(b[0].getDate() + 2);
            }
            if (b[0].getDay() === 5) {
                leftDay.setDate(b[0].getDate() + 3);
            }

            rightDay.setDate(b[1].getDate() + 1);
            if (b[1].getDay() === 6) {
                rightDay.setDate(b[1].getDate() + 2);
            }
            if (b[1].getDay() === 5) {
                rightDay.setDate(b[1].getDate() + 3);
            }


            for (var i = 0; i < linecharts.length; i++) {
                try {
                    linecharts[i].showOnly(b, 1);
                } catch (err) {
                    console.log("error caught -" + err);
                }
            }

        }

    });

}

OverviewChart.prototype.addLine = function(options) {
    var stockObject = this.stockObject = options.stockObject;
    var data = this.data = stockObject.data;
    var id = this.id = options.id;

    this.chartContainer.append("path")
        .attr("class", "line")
        .attr("clip-path", "url(#clip)")
        .data([this.data])
        .attr("d", this.line)
        .attr("stroke", this.color(options.id))
        .attr("fill", "transparent")
        .attr("stroke-width", "1.5px")
        .attr("opacity", 0.8).attr("z-index", 1);

};