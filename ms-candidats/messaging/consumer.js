const amqp = require('amqplib');

const QUEUE_NOUVELLE = 'queue.offre.nouvelle';
const QUEUE_SUPPRIMEE = 'queue.offre.supprimee';

async function connectRabbitMQ() {
  try {
const connection = await amqp.connect(`amqp://${process.env.RABBITMQ_HOST || 'localhost'}`);    const channel = await connection.createChannel();

    await channel.assertQueue(QUEUE_NOUVELLE, { durable: true });
    await channel.assertQueue(QUEUE_SUPPRIMEE, { durable: true });

    console.log('RabbitMQ connecté - en attente de messages...');

    // Scénario async 1 : nouvelle offre créée
    channel.consume(QUEUE_NOUVELLE, (msg) => {
      if (msg !== null) {
        const offre = JSON.parse(msg.content.toString());
        console.log('Nouvelle offre reçue:', offre.titre);
        console.log('Notification aux candidats pour l offre:', offre.entreprise);
        channel.ack(msg);
      }
    });

    // Scénario async 2 : offre supprimée
    channel.consume(QUEUE_SUPPRIMEE, (msg) => {
      if (msg !== null) {
        const offreId = msg.content.toString();
        console.log('Offre supprimée ID:', offreId);
        console.log('Mise à jour des candidats concernés...');
        channel.ack(msg);
      }
    });

  } catch (err) {
    console.error('Erreur RabbitMQ:', err.message);
    setTimeout(connectRabbitMQ, 5000);
  }
}

module.exports = { connectRabbitMQ };