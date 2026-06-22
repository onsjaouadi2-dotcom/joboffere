const express = require('express');
const mongoose = require('mongoose');
const cors = require('cors');
const candidatRoutes = require('./routes/candidatRoutes');
const eurekaClient = require('./eureka');
const { connectRabbitMQ } = require('./messaging/consumer');

const app = express();
const PORT = 8082;

app.use(cors());
app.use(express.json());

// MongoDB
mongoose.connect(process.env.MONGO_URL || 'mongodb://localhost:27017/jobboard_candidats')  .then(() => console.log('MongoDB connecté'))
  .catch(err => console.error('Erreur MongoDB:', err));

// Routes
app.use('/candidats', candidatRoutes);

// Démarrage
app.listen(PORT, () => {
  console.log(`MS Candidats démarré sur le port ${PORT}`);
  eurekaClient.start(error => {
    if (error) console.error('Erreur Eureka:', error);
    else console.log('Enregistré dans Eureka !');
  });
  connectRabbitMQ();
});