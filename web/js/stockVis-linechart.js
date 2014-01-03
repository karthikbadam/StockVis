//LineChart class

function LineChart(options) {

    var _self = this;
    var stockObject = this.stockObject = options.stockObject;
    var stockColumns = this.stockColumns = options.columns;
    var charts = this.charts = options.charts;
    var color = this.color = options.color;
    var id = this.id = options.id;
    var data = this.data = stockObject.data;
    var dataFiltered = this.dataFiltered = stockObject.data;
    var stockName = this.stockName = options.name;
    var stockSymbol = this.stockSymbol = options.symbol;
    var trainingStocks = this.trainingStocks = options.trainingStocks;
    var spatialPrediction = this.spatialPrediction = options.spatialPrediction;
    var temporalPredictors = this.temporalPredictors = options.temporalPredictors;
    var numberOfPredictionsMade = this.numberOfPredictionsMade = 0;
    var margin = this.margin = {
        top: 60,
        right: 40,
        bottom: 60,
        left: 40
    };
    
    var tomorrow = this.tomorrow = new Date();
    
    var dataFilteredForPrediction = this.dataFilteredForPrediction = this.dataFiltered;
    
    var svgWidth = 2*$("#linechart-viz").width()/3 - margin.left - margin.right;
    
    var predictionRects = this.predictionRects = [];
    
    var width = (480 - margin.left - margin.right),
        height = (250 - margin.top - margin.bottom);

    this.width = width;
    this.height = height;

    this.div = d3.select("#linecharts").append("div")
        .attr("class", "stockChart").attr("id", "ID"+id);
    
    this.div.append("div").attr("class", "expandClass")
            .on("click", expandChart);
    
    function expandChart() {
        //alert("expand");
        console.log("width"+ 49 * $("#linechart-viz").width()/100 +","+ $("#ID"+id).width());
        if ($("#ID"+id).width() === 480) {
            $("#ID"+id).width(Math.round(49 * $("#linechart-viz").width()/100));
        } else if ($("#ID"+id).width() === Math.round(49*$("#linechart-viz").width()/100)) {
            $("#ID"+id).width(480);
        }
    }

    var svg = this.svg = this.div.append("svg")
        .attr("width", svgWidth + margin.left - margin.right)
        .attr("height", height + margin.top + margin.bottom)
        .append("g")
        .attr("transform", "translate(" + (margin.left) + "," + margin.top + ")");

    //prediction svg 
//    
//    var predictionSVG = this.predictionSVG = this.div.append("svg")
//        .attr("class", "predictionChart")
//        .attr("width", svgWidth - width + margin.left + margin.right)
//        .attr("height", (height + margin.top + margin.bottom))
//        .append("g")
//        .attr("transform", "translate(" + margin.left + "," + margin.top + ")");

    
    //volume bar chart svg
    
    var volumeSVG = this.volumeSVG = this.div.append("svg")
        .attr("class", "volumeBar")
        .attr("width", width + margin.left + margin.right)
        .attr("height", (height + margin.top + margin.bottom) / 5)
        .append("g")
        .attr("transform", "translate(" + (margin.left) + "," + margin.top / 4 + ")");


    
    //sets domain for Axis x - date and Axis y - Adj close
    
    var x = this.x = d3.time.scale()
        .range([0, width]);

    var y = this.y = d3.scale.linear()
        .range([height, 0]);

    x.domain(d3.extent(this.dataFiltered, function(stock) {
        return stock[stockColumns[0]];
    }));
    y.domain(d3.extent(this.dataFiltered, function(stock) {
        return stock[stockColumns[1]];
    }));

    
    //creates x and y axis
    
    this.xAxis = d3.svg.axis()
        .scale(this.x)
        .orient("bottom").ticks(4)
        .tickFormat(function(d) {
            return d3.time.format('%b%d/%y')(new Date(d));
        });

    this.yAxis = d3.svg.axis()
        .scale(this.y)
        .orient("left").ticks(6);
    var yAxis = this.yAxis;


    //creates the mapping function for path line in SVG
    
    var line = this.line = d3.svg.line()
        .interpolate("Monotone")
        .x(function(d) {
            return x(d[stockColumns[0]]);
        })
        .y(function(d) {
            return y(d[stockColumns[1]]);
        });

    
    //general definitions to keep everything within boundaries
    
    this.svg.append("defs")
        .append("clipPath").attr("id", "clip-" + this.id)
        .append("rect")
        .attr("width", this.width).attr("height", this.height);


    //creates arrow head for user drawn prediction line! 
    
    this.svg.selectAll("marker")
        .data(["marker", "licensing", "resolved"])
        .enter().append("svg:marker")
        .attr("id", String)
        .attr("viewBox", "0 -5 10 10")
        .attr("refX", 0)
        .attr("refY", 0)
        .attr("markerWidth", 8)
        .attr("markerHeight", 8)
        .attr("orient", "auto")
        .append("svg:path")
        .attr("d", "M0,-5L10,0L0,5");

    
    //draws the path line    
    
    this.chartContainer = this.svg.append("g")
        .attr("class", "linechart")
        .attr("width", this.width).attr("height", this.height);

    this.chartContainer.append("path")
        .attr("class", "line")
        .attr("clip-path", "url(#clip-" + this.id + ")")
        .data([this.dataFiltered])
        .attr("d", this.line)
        .attr("stroke", color(this.id))
        .attr("fill", "transparent")
        .attr("stroke-width", "1.5px")
        .attr("z-index", 1);


    //draws the axis    
    
    this.chartContainer.append("g")
        .attr("class", "x axis")
        .attr("transform", "translate(0," + height + ")")
        .call(this.xAxis);

    this.chartContainer.append("g")
        .attr("class", "y axis")
        .call(this.yAxis)
        .append("text")
        .attr("transform", "rotate(-90)")
        .attr("y", 4)
        .attr("dy", ".71em")
        .style("text-anchor", "end");

    //draws the text -- name of the stock    
    this.chartContainer.append("text").attr("class", "Stock-title")
        .attr("transform", "translate(" + 10 + ",0)")
        .text(this.stockName)
        .attr("stroke-opacity", 0.3)
        .attr("fill-opacity", 0.3)
        .attr("stroke", color(this.id))
        .attr("font-size", "11px");

    
    //parameters to show the prediction line at end of each chart
    
    var lastValueY = this.lastValueY = y(data[0][stockColumns[1]]);
    var lastValueX = this.lastValueX = x(data[0][stockColumns[0]]);
    var stockMaxValue = this.stockMaxValue = y.domain()[1];
    var stockMinValue = this.stockMinValue = y.domain()[0];
    var closingValue = this.closingValue = data[0][stockColumns[1]];

    var predictionValueX = this.predictionValueX = this.lastValueX;
    var predictionValueY = this.predictionValueY = this.lastValueY;

    //creates y-axis for the volume bar chart 
    
    var volumeY = this.volumeY = d3.scale.linear().range([height / 4, 0]);
    volumeY.domain(d3.extent(data, function(stock) {
        return stock["Volume"];
    }));


    //creating Volume SVG
    
    this.volumeSVG.selectAll(".bar")
        .data(this.data)
        .enter().append("rect")
        .attr("class", "bar")
        .attr("x", function(d) {
            return x(d[stockColumns[0]]);
        })
        .attr("width", 2)
        .attr("y", function(d) {
            return volumeY(d["Volume"]);
        })
        .attr("height", function(d) {
            return height / 4 - volumeY(d["Volume"]);
        })
        .attr("fill", color(this.id))
        .attr("fill-opacity", 0.3);


    //draws a rectangle at the right of each line chart for predictions
    
    var rect_offsetX = 5;
    var rectangle_width = this.margin.right + 5;
    var rectangle_height = height + margin.top + margin.bottom;
    
    var numberOfPredictions = 15;
    for (var i = 0; i < numberOfPredictions; i++) {
        var rect = this.svg.append("svg:rect")
            .attr("class", "rect")
            .attr("transform", "translate(" + (width + i*rectangle_width - rect_offsetX) + "," + (-margin.top) + ")")
            .attr("width", rectangle_width)
            .attr("height", rectangle_height)
            .on("mousedown", mousedown)
            .on("mousemove", mousemove)
            .on("mouseup", mouseup);
        this.predictionRects.push(rect);   
    }
    
//    var rect = this.rect = this.svg.append("svg:rect")
//        .attr("class", "rect")
//        .attr("transform", "translate(" + (width - rect_offsetX) + "," + -rect_offsetY + ")")
//        .attr("width", rectangle_width)
//        .attr("height", rectangle_height)
//        .on("mousedown", mousedown)
//        .on("mousemove", mousemove)
//        .on("mouseup", mouseup);

    //creates the variable for the prediction line -- 
    //variable updated when user actually draws a prediction    
    var draw = svg.append("line").attr("id", "prediction")
        .attr("x1", this.lastValueX)
        .attr("y1", this.lastValueY)
        .attr("x2", this.lastValueX)
        .attr("y2", this.lastValueY)
        .attr("marker-end", "url(#marker)");
    

    //prediction draw handlers 
    var predictMouseClicked = false;
    var userPredicted = this.userPredicted = false;
    var lineLength = this.lineLength = 0;

    function mousedown() {
        predictMouseClicked = true;
        _self.lineLength = 0;
    }

    function mousemove() {
        var m = d3.mouse(this);
        if (predictMouseClicked) {
            draw.attr("x2", (width - 2*rect_offsetX + (_self.numberOfPredictionsMade +1)*rectangle_width))
                .attr("y2", (m[1] - margin.top))
                .attr("stroke", color(_self.id));
            
            _self.predictedValueX = width - rect_offsetX + (_self.numberOfPredictionsMade + 1)*rectangle_width;    
            _self.predictedValueY = m[1] - margin.top;    
            
            var predictedY = _self.predictedY = _self.stockMaxValue - (_self.stockMaxValue - _self.stockMinValue) * (((m[1] - margin.top)) / (height));
            _self.lineLength = 100 * ((predictedY - _self.closingValue) / _self.closingValue);
        }
        
        svg.on("mouseup", mouseup);
    }

    function mouseup() {
        if (!predictMouseClicked) {
            return;
        }

        console.log("mouse up");
        predictMouseClicked = false;
        _self.userPredicted = true;
        
        var error = Math.abs((_self.predictedY - _self.tomorrowValue) * 100 / _self.tomorrowValue);
        console.log("Prediction error= " + error + "%" +" actual value " +_self.tomorrowValue+" predicted value "+_self.predictedY);
        if (error < 10) {
            var score = Number($('#score_value').text());
            $('#score_value').html((score + 1));
            console.log("score - " + score);
        }
        
        

        var count=0;
        for (var i = 0; i < _self.charts.length; i++) {
            if (_self.charts[i].userPredicted === true) {
                count++;
                continue;
            } else {
                break;
            }
        }

        if (count === _self.charts.length) {
           for (var i = 0; i < _self.charts.length; i++) {
               _self.charts[i].moveToNextInstance();
           }
           return;
        }
        
        if (_self.startedPredictions) {
            var actualValue = _self.tomorrowValue;
            return;
        }
        
        //pop up a confirm dialog box for spatial prediction
        $("#dialog-confirm").dialog({
            resizable: false,
            height: 140,
            modal: true,
            open: function(event, ui) {
                var textarea = '<p id="predictionText"><span class="ui-icon ui-icon-alert" style="float: left; margin: 0 7px 20px 0;"></span>Do you want to predict other stocks?</p>';
                if ($("#predictionText").contents().length < 1) {
                    $("#dialog-confirm").append(textarea);
                }
            },
            buttons: {
                OK: function() {
                    $(this).dialog("close");
                    //do something
                    var predictions = spatialPrediction.getPredictions(_self.lineLength, stockSymbol);
                    for (var i = 0; i < _self.charts.length; i++) {
                        for (var j = 0; j < predictions.arrays.length; j++) {
                            _self.charts[i].addPrediction(predictions.arrays[j], predictions.opacities[j]);
                        }
                    }
                },
                Cancel: function() {
                    $(this).dialog("close");
                }
            }
        });
    }

}

//move to next instance
LineChart.prototype.moveToNextInstance = function() {
    this.tomorrowValue = this.predictedY;
    var stockData = {};
    stockData[this.stockColumns[1]] = this.tomorrowValue;
    stockData[this.stockColumns[0]] = this.tomorrow;
    
    var b = this.tomorrow;
    var tomorrow = this.tomorrow;
    //find the date of next day
    tomorrow.setMonth(b.getMonth());
    tomorrow.setFullYear(b.getFullYear());

    tomorrow.setDate(b.getDate() + 1);
    if (b.getDay() === 6) {
        tomorrow.setDate(b.getDate() + 2);
    }
    if (b.getDay() === 5) {
        tomorrow.setDate(b.getDate() + 3);
    }
    //go through the data to find the actual value
    this.tomorrowValue = 0;
    for (var i = 0; i < this.data.length; i++) {
        var d = this.data[i];
        if (d[this.stockColumns[0]].getDate() === tomorrow.getDate() && d[this.stockColumns[0]].getMonth() === tomorrow.getMonth() && d[this.stockColumns[0]].getFullYear() === tomorrow.getFullYear()) {
            this.tomorrowValue = d[this.stockColumns[1]];
            console.log("OWO");
            break;
        }
    }
    
    
    var prData = [];
    prData.push(stockData);
    for (var i = 0; i < 15; i++) {
        prData.push(this.dataFilteredForPrediction[i]);
    }
    this.dataFilteredForPrediction = prData;
    
    var input = new Array(15);
    for (var i = 0; i < 15; i++) {
        input[i] = this.stockObject.normalizeValue(prData[i][this.stockColumns[1]]);
    }
    
    this.numberOfPredictionsMade++;
    
    this.svg.append("line")
        .attr("class", "userPredictionLine")
        .attr("x1", this.lastValueX)
        .attr("y1", this.lastValueY)
        .attr("x2", this.predictedValueX)
        .attr("y2", this.predictedValueY)
        .attr("stroke", this.color(this.id))
        .attr("stroke-opacity", 0.8)
        .attr("stroke-width", "2px");
     
    this.svg.selectAll(".temporalPredictionLine")
        .attr("stroke-opacity", 0.1); 
    
    this.svg.selectAll(".predictionLine")
        .attr("stroke-opacity", 0.03); 
       
    this.lastValueX = this.predictedValueX;
    this.lastValueY = this.predictedValueY;
    
    var predictor = this.temporalPredictors[this.stockSymbol];
    var output = predictor.predict(input);
    this.currentPrediction = this.stockObject.deNormalize(output[0]);
    //console.log("prediction is "+((this.currentPrediction - tomorrowValue)*100/this.currentPrediction));
    this.svg.append("line")
        .attr("class", "temporalPredictionLine")
        .attr("x1", this.lastValueX)
        .attr("y1", this.lastValueY)
        .attr("x2", this.lastValueX + this.margin.right)
        .attr("y2", this.y(this.currentPrediction))
        .attr("stroke", this.color(this.id))
        .attr("stroke-opacity", 0.2)
        .attr("stroke-width", "2px");
    
    
    
    console.log("after all prediction --"+this.lastValueX);
    this.svg.select("#prediction")
        .attr("x1", this.lastValueX)
        .attr("y1", this.lastValueY)
        .attr("x2", this.lastValueX)
        .attr("y2", this.lastValueY);
     
     this.userPredicted = false;
     this.startedPredictions = false;
     
};

//brushes each line chart based on the region selected on the overview
LineChart.prototype.showOnly = function(b, empty) {
    
    this.numberOfPredictionsMade = 0;
    this.userPredicted = false;
    this.startedPredictions = false;
    
    var x = this.x;
    var stockColumns = this.stockColumns;
    var tomorrow = this.tomorrow = new Date();
    var y = this.y;
    var dataFiltered = this.dataFiltered = this.stockObject.getFilteredData(b);
    
    if (dataFiltered.length < 0) {
        return;
    }
    
    this.dataFilteredForPrediction = this.dataFiltered;
    
    this.x.domain(b);
    this.chartContainer.select(".x.axis").call(this.xAxis);

    y.domain(d3.extent(dataFiltered, function(stock) {
        return stock[stockColumns[1]];
    }));
    
    y.domain([y.domain()[0] - y.domain()[0] / 50, y.domain()[1] + y.domain()[1] / 50]);
    this.chartContainer.select(".y.axis").call(this.yAxis);

    
    //parameters to find the ending value of each chart
    
    var lastValueY = this.lastValueY = y(dataFiltered[0][stockColumns[1]]);
    var lastValueX = this.lastValueX = x(dataFiltered[0][stockColumns[0]]);

    this.closingValue = dataFiltered[0][stockColumns[1]];
    this.stockMaxValue = y.domain()[1];
    this.stockMinValue = y.domain()[0];

    this.svg.select("#prediction")
        .attr("x1", lastValueX)
        .attr("y1", lastValueY)
        .attr("x2", lastValueX)
        .attr("y2", lastValueY);


    var height = this.height;
    var volumeY = this.volumeY.domain(d3.extent(dataFiltered, function(stock) {
        return stock["Volume"];
    }));

    
    //updates volume chart below the line chart
    
    this.volumeSVG.selectAll(".bar").remove();
    this.volumeSVG.selectAll(".bar")
        .data(dataFiltered)
        .enter().append("rect")
        .attr("class", "bar")
        .attr("x", function(d) {
            return x(d[stockColumns[0]]);
        })
        .attr("width", 4)
        .attr("y", function(d) {
            return volumeY(d["Volume"]);
        })
        .attr("height", function(d) {
            return height / 4 - volumeY(d["Volume"]);
        })
        .attr("fill", color(this.id))
        .attr("fill-opacity", 0.6);

    this.chartContainer.select("path")
        .attr("clip-path", "url(#clip-" + this.id + ")")
        .data([dataFiltered])
        .attr("d", this.line);


    //re-renders each dot on the linechart    
    
    this.chartContainer.selectAll(".dot").remove();
    this.chartContainer.selectAll(".dot")
        .data(dataFiltered)
        .enter()
        .append("circle")
        .attr("class", "dot")
        .attr("cx", this.line.x())
        .attr("cy", this.line.y())
        .attr("r", 3)
        .attr("stroke", this.color(this.id))
        .attr("fill", "transparent")
        .attr("stroke-opacity", 0.3);


    //finds temporal prediction lines -- one per each linechart
    this.svg.selectAll(".userPredictionLine").remove();

    this.svg.selectAll(".predictionLine").remove();

    var input = new Array(15);
    for (var i = 0; i < 15; i++) {
        input[i] = this.stockObject.normalizeValue(this.dataFiltered[i][this.stockColumns[1]]);
    }
    //find the date of next day
    tomorrow.setMonth(b[1].getMonth());
    tomorrow.setFullYear(b[1].getFullYear());

    tomorrow.setDate(b[1].getDate() + 1);
    if (b[1].getDay() === 6) {
        tomorrow.setDate(b[1].getDate() + 2);
    }
    if (b[1].getDay() === 5) {
        tomorrow.setDate(b[1].getDate() + 3);
    }
    //go through the data to find the actual value
    this.tomorrowValue = 0;
    for (var i = 0; i < this.data.length; i++) {
        var d = this.data[i];
        if (d[stockColumns[0]].getDate() === tomorrow.getDate() && d[stockColumns[0]].getMonth() === tomorrow.getMonth() && d[stockColumns[0]].getFullYear() === tomorrow.getFullYear()) {
            this.tomorrowValue = d[stockColumns[1]];
            break;
        }
    }
    
    this.svg.selectAll(".temporalPredictionLine").remove();
    var predictor = this.temporalPredictors[this.stockSymbol];
    var output = predictor.predict(input);
    this.currentPrediction = this.stockObject.deNormalize(output[0]);
    
    //console.log("prediction is "+((this.currentPrediction - tomorrowValue)*100/this.currentPrediction));
    this.svg.append("line")
        .attr("class", "temporalPredictionLine")
        .attr("x1", this.lastValueX)
        .attr("y1", this.lastValueY)
        .attr("x2", this.lastValueX + this.margin.right)
        .attr("y2", this.y(this.currentPrediction))
        .attr("stroke", this.color(this.id))
        .attr("stroke-opacity", 0.4)
        .attr("stroke-width", "2px");

};

// Adds spatial predictions
LineChart.prototype.addPrediction = function(predictionArray, opacity) {
    
    this.startedPredictions = true;
    var stockIndex = this.trainingStocks.indexOf(this.stockSymbol);
    var value = this.closingValue + this.closingValue * predictionArray[stockIndex] / 100;
    //console.log("Index "+stockIndex+" Value "+value);
    this.svg.append("line")
        .attr("class", "predictionLine")
        .attr("x1", this.lastValueX)
        .attr("y1", this.lastValueY)
        .attr("x2", this.lastValueX + this.margin.right)
        .attr("y2", this.y(value))
        .attr("stroke", this.color(this.id))
        .attr("stroke-opacity", opacity);
};