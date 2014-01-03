//Class Spatial Prediction

function SpatialPrediction(options) {
    var weights = this.weights = options.weights;
    var trainingStocks = this.trainingStocks = options.trainingStocks;
    var stockSymbols = this.stockSymbols = options.stockSymbols;

    console.log(weights);
    var weightsSize = this.weightsSize = weights.length;
    console.log(weightsSize);

    var stocksSize = this.stocksSize = stockSymbols.length;
    console.log(stocksSize);

    var predictionArrays =  this.predictionArrays;

}

SpatialPrediction.prototype.getPredictions = function(prediction, stock_symbol) {
    var predictionArrays = this.predictionArrays = [];
    var predictionOpacities = [];
    var stockIndex = this.trainingStocks.indexOf(stock_symbol);
    for (var i = 0; i < this.weightsSize; i++) {
        var distance = Math.abs(this.weights[i][stockIndex]- prediction);
        if ( distance < 1 ) {
            var opacity = 1 - distance;
            predictionArrays.push(this.weights[i]);
            predictionOpacities.push(opacity);
        }
    }

    var returnVal = [];
    returnVal.arrays = predictionArrays;     
    returnVal.opacities = predictionOpacities; 
    return returnVal;
};
