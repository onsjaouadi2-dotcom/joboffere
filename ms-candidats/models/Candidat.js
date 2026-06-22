const mongoose = require('mongoose');

const candidatSchema = new mongoose.Schema({
  nom: { type: String, required: true },
  prenom: { type: String, required: true },
  email: { type: String, required: true, unique: true },
  telephone: String,
  competences: [String],
  experience: Number,
  offreId: Number,
  cv: String
}, { timestamps: true });

module.exports = mongoose.model('Candidat', candidatSchema);