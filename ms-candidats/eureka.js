const Eureka = require('eureka-js-client').Eureka;

const client = new Eureka({
  instance: {
    app: 'ms-candidats',
    hostName: process.env.HOSTNAME || 'localhost',
    ipAddr: '127.0.0.1',
    port: { '$': 8082, '@enabled': true },
    vipAddress: 'ms-candidats',
    dataCenterInfo: {
      '@class': 'com.netflix.appinfo.InstanceInfo$DefaultDataCenterInfo',
      name: 'MyOwn'
    }
  },
  eureka: {
    host: process.env.EUREKA_HOST || 'localhost',
    port: 8761,
    servicePath: '/eureka/apps/'
  }
});

module.exports = client;