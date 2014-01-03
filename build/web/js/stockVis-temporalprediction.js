//Class Temporal Prediction

function TemporalPrediction(options) {
//    var layers = this.layers = [];
//    layers[0] = ENCOG.BasicLayer.create(ENCOG.ActivationTANH.create(), 15, 1);
//    layers[1] = ENCOG.BasicLayer.create(ENCOG.ActivationTANH.create(), 41, 1);
//    layers[2] = ENCOG.BasicLayer.create(ENCOG.ActivationTANH.create(), 41, 1);
//    layers[3] = ENCOG.BasicLayer.create(ENCOG.ActivationTANH.create(), 1, 1);
//    
//    var network = this.network =  ENCOG.BasicNetwork.create(layers);
//   
//    network.randomize();
//    var result = ENCOG.EGFILE.save(network);
//   
 
    var stockName = this.stockName = options.stock_name;
    var encogFileContent = this.encogFileContent = options.encog_file; 
   
    var network = this.network = ENCOG.EGFILE.load(encogFileContent);
    
    var result = ENCOG.EGFILE.save(network);
    
    console.log("result --"+result);
};

TemporalPrediction.prototype.predict = function(input) {
    var network = this.network;
    
    var output = new Array(1);
    network.compute(input, output);
   
    return output;
};

