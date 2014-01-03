//Stock Class

function Stock(options) {
    var data = this.data = options.data;
    var companyName = this.companyName = options.companyName;
    var symbol = this.symbol = options.symbol;
    var startDate = this.startDate = options.startDate; 
    var min = this.min = 0;
    var max = this.max = 0;
    var normalization = this.normalization = 0;
    var dataFiltered = this.dataFiltered = [];
} 

Stock.prototype.normalize = function(close_values) {
    var data = this.data;
    var max = Math.max.apply(Math, close_values) + 0.0001;
    var min = Math.min.apply(Math, close_values) - 0.0001;

    for (var i= 0; i < close_values.length; i++) {
        data[i].normalized = (data[i].normalized - min)/(max - min);
    }
    
    min = this.min = 100000;
    max = this.max = 0;
    for (var i = 0; i < data.length; i++) {
        if( this.min > data[i]['Adj Close'] && data[i].Date >  this.startDate ) {
            this.min = data[i]['Adj Close'];
        }
        if( this.max < data[i]['Adj Close'] && data[i].Date >  this.startDate ) {
            this.max = data[i]['Adj Close'];
        }   
    }
    
    this.min = this.min - 0.0002;
    this.normalization = this.max - this.min + 0.0003;
    console.log(this.companyName +" min -"+this.min+" norm -"+this.normalization);
    
};

Stock.prototype.deNormalize = function(value) {
    var normalization = this.normalization; 
    var min = this.min; 
    
    return min+value*normalization; 
};

Stock.prototype.normalizeValue = function(value) {
    var normalization = this.normalization; 
    var min = this.min; 
    
    return (value - min)/normalization; 
};

Stock.prototype.getFilteredData = function(brush) {
    var dataFiltered = this.dataFiltered = this.data.filter(function(d, i) {
        if ( (d[stockColumns[0]] >= brush[0]) && (d[stockColumns[0]] <= brush[1]) ) {
            return d[stockColumns[1]];
        }
    });
    
   return dataFiltered; 
}; 