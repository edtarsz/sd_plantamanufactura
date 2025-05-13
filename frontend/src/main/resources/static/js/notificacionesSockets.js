let stompClient = null;

export function conectarNotificaciones(onMensajeRecibido) {
  // const socket = new SockJS("http://localhost:8222/ws-notificaciones");
const socket = new WebSocket("ws://localhost:8222/ws-notificaciones/websocket");


  stompClient = Stomp.over(socket);

  stompClient.connect({}, function (frame) {
    console.log("✅ Conectado a WebSocket:", frame);

    stompClient.subscribe("/topic/notifications", function (msg) {
      const data = JSON.parse(msg.body);
      console.log("🔔 Notificación recibida:", data.message);
      onMensajeRecibido(data.message);
    });
  }, function (error) {
    console.error("❌ Error de conexión WebSocket:", error);
  });
}

export function enviarNotificacion(mensaje) {
  if (stompClient && stompClient.connected) {
    stompClient.send("/app/notify", {}, JSON.stringify({ message: mensaje }));
  } else {
    console.warn("⚠️ WebSocket no está conectado. No se pudo enviar la notificación.");
  }
}
