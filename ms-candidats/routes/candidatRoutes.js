const express = require('express');
const router = express.Router();
const Candidat = require('../models/Candidat');
const axios = require('axios');

// GET tous les candidats
router.get('/', async (req, res) => {
  try {
    const candidats = await Candidat.find();
    res.json(candidats);
  } catch (err) {
    res.status(500).json({ message: err.message });
  }
});

// GET candidat par ID
router.get('/:id', async (req, res) => {
  try {
    const candidat = await Candidat.findById(req.params.id);
    if (!candidat) return res.status(404).json({ message: 'Candidat non trouvé' });
    res.json(candidat);
  } catch (err) {
    res.status(500).json({ message: err.message });
  }
});

// GET candidats par offre
router.get('/offre/:offreId', async (req, res) => {
  try {
    const candidats = await Candidat.find({ offreId: req.params.offreId });
    res.json(candidats);
  } catch (err) {
    res.status(500).json({ message: err.message });
  }
});

// POST créer candidat
router.post('/', async (req, res) => {
  try {
    const candidat = new Candidat(req.body);
    const saved = await candidat.save();
    res.status(201).json(saved);
  } catch (err) {
    res.status(400).json({ message: err.message });
  }
});

// PUT modifier candidat
router.put('/:id', async (req, res) => {
  try {
    const updated = await Candidat.findByIdAndUpdate(
      req.params.id, req.body, { new: true }
    );
    res.json(updated);
  } catch (err) {
    res.status(400).json({ message: err.message });
  }
});

// DELETE supprimer candidat
router.delete('/:id', async (req, res) => {
  try {
    await Candidat.findByIdAndDelete(req.params.id);
    res.status(204).send();
  } catch (err) {
    res.status(500).json({ message: err.message });
  }
});

// Scénario Feign 2 : récupérer l'offre d'un candidat
router.get('/:id/offre', async (req, res) => {
  try {
    const candidat = await Candidat.findById(req.params.id);
    const offre = await axios.get(`http://localhost:8081/offres/${candidat.offreId}`);
    res.json({ candidat, offre: offre.data });
  } catch (err) {
    res.status(500).json({ message: err.message });
  }
});

module.exports = router;