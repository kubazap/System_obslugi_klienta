/* =========================================================================
   1.  KONFIGURACJA CSRF
   ========================================================================= */
const CSRF_TOKEN  = document.querySelector('meta[name="_csrf"]').content;
const CSRF_HEADER = document.querySelector('meta[name="_csrf_header"]').content;

/* =========================================================================
   2.  POMOCNICZE FUNKCJE
   ========================================================================= */
const initials = (f, l) => (f && l ? (f[0] + l[0]).toUpperCase() : '');

const hhmm = iso =>
  new Date(iso).toLocaleTimeString('pl-PL', { hour: '2-digit', minute: '2-digit' });

function el(tag, cls = '', text = '') {
  const node = document.createElement(tag);
  if (cls) node.className = cls;
  if (text) node.textContent = text;
  return node;
}

/* =========================================================================
   3.  PODŚWIETLANIE WYBRANEJ ROZMOWY
   ========================================================================= */
function markActive(item) {
  document
    .querySelectorAll('#conversationList .conversation-item')
    .forEach(i => i.classList.remove('active'));
  item.classList.add('active');
}

/* =========================================================================
   4.  ŁADOWANIE LISTY KONWERSACJI
   ========================================================================= */
function lastMsgTime(iso) {
  if (!iso) return '';
  const d = new Date(iso);
  const now = new Date();

  const sameDay =
        d.getDate()     === now.getDate()    &&
        d.getMonth()    === now.getMonth()   &&
        d.getFullYear() === now.getFullYear();

  return sameDay
      ? d.toLocaleTimeString('pl-PL', { hour: '2-digit', minute: '2-digit' })
      : d.toLocaleDateString('pl-PL', { day: '2-digit', month: '2-digit' });
}

/*  ─────────────────────────────────────────────────────────── */
function loadConversations(myId) {
  const panel = document.getElementById('conversationList');
  panel.textContent = 'Ładowanie…';

  fetch(`/messages/conversations/${myId}`)
    .then(r => r.json())
    .then(list => {
      panel.innerHTML = '';
      if (!list.length) {
        panel.textContent = 'Brak konwersacji.';
        return;
      }

      list.forEach((c, idx) => {
        /* ---------- wiersz + klik ---------- */
        const row = el('div', 'conversation-item align-items-center');
        row.onclick = () => {
          markActive(row);
          showConversation(myId, c.rozmowcaId, c.rozmowcaImie, c.rozmowcaNazwisko);
        };

        /* ---------- avatar (inicjały) ---------- */
        row.appendChild(
          el('span', 'avatar d-flex align-items-center justify-content-center',
             initials(c.rozmowcaImie, c.rozmowcaNazwisko))
        );

        /* ---------- dane rozmówcy + podgląd tekstu ---------- */
        const body = el('div', 'ml-2 flex-grow-1');
        body.innerHTML =
          `<div class="name">${c.rozmowcaImie} ${c.rozmowcaNazwisko}</div>
           <small class="text-muted preview">${c.ostatniaWiadomoscTresc ?? ''}</small>`;
        row.appendChild(body);

        /* ---------- czas / data ostatniej wiadomości ---------- */
        row.appendChild(
          el('div', 'ml-auto pl-5 small text-muted time-col',
             lastMsgTime(c.ostatniaWiadomoscCzas))
        );

        panel.appendChild(row);

        /* automatycznie otwórz pierwszą konwersację */
        if (idx === 0) {
          markActive(row);
          showConversation(myId, c.rozmowcaId, c.rozmowcaImie, c.rozmowcaNazwisko);
        }
      });
    })
    .catch(() => (panel.textContent = 'Błąd konwersacji'));
}

/* =========================================================================
   5.  POKAZANIE KONKRETNEGO WĄTKU
   ========================================================================= */
function showConversation(myId, partnerId, imie, nazwisko) {
  document.getElementById('chatReceiverName').textContent = `${imie} ${nazwisko}`;
  document.getElementById('receiverIdMessage').textContent = partnerId;

  const box = document.getElementById('conversationMessages');
  box.textContent = 'Ładowanie…';

  fetch(`/messages/${myId}/${partnerId}`)
    .then(r => (r.ok ? r.json() : Promise.reject()))
    .then(msgs => {
      box.innerHTML = '';
      if (!msgs.length) {
        box.textContent = 'Brak wiadomości.';
        return;
      }

      const today = new Date();

      msgs
        .sort((a, b) => new Date(a.timestamp) - new Date(b.timestamp))
        .forEach(m => {
          /* wrapper – ułatwia wyrównanie czasu pod bańką */
          const wrap = el(
            'div',
            'msg-wrap ' + (m.senderId === myId ? 'sent' : 'received')
          );

          /* bańka */
          const bubble = el('div', 'message-item', m.content);
          wrap.appendChild(bubble);

          /* czas – HH:MM (jeśli dziś) lub DD-MM HH:MM */
          const dt = new Date(m.timestamp);
          const sameDay =
            dt.getDate() === today.getDate() &&
            dt.getMonth() === today.getMonth() &&
            dt.getFullYear() === today.getFullYear();

          const when = sameDay
            ? dt.toLocaleTimeString('pl-PL', { hour: '2-digit', minute: '2-digit' })
            : dt.toLocaleString('pl-PL', {
                day: '2-digit',
                month: '2-digit',
                hour: '2-digit',
                minute: '2-digit'
              });

          wrap.appendChild(el('small', 'msg-time text-muted', when));
          box.appendChild(wrap);
        });

      box.scrollTop = box.scrollHeight;
    })
    .catch(() => (box.textContent = 'Błąd podczas pobierania wiadomości'));
}

/* =========================================================================
   6.  WYSŁANIE NOWEJ WIADOMOŚCI
   ========================================================================= */
function sendNewMessage() {
  const senderId   = +document.body.dataset.senderId;
  const receiverId = +document.getElementById('receiverIdMessage').textContent;
  const textarea   = document.getElementById('newMessage');
  const content    = textarea.value.trim();

  if (!receiverId || !content) return;

  fetch('/messages', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json', [CSRF_HEADER]: CSRF_TOKEN },
    body: JSON.stringify({ senderId, receiverId, content })
  })
    .then(() => {
      textarea.value = '';
      showConversation(senderId, receiverId); // odśwież wątek
      loadConversations(senderId);            // odśwież listę
    })
    .catch(console.error);
}

/* =========================================================================
   7.  START
   ========================================================================= */
document.addEventListener('DOMContentLoaded', () => {
  const me = +document.body.dataset.senderId;
  loadConversations(me);

  document.getElementById('newMessage').addEventListener('keydown', e => {
    if (e.key === 'Enter' && (e.ctrlKey || e.metaKey)) {
      sendNewMessage();
      e.preventDefault();
    }
  });
});