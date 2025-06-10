/* =========================================================================
   0.  KONFIGURACJA CSRF
   ========================================================================= */
const CSRF_TOKEN_  = document.querySelector('meta[name="_csrf"]').content;
const CSRF_HEADER_ = document.querySelector('meta[name="_csrf_header"]').content;


/* =========================================================================
   1.  POMOCNICZE FUNKCJE
   ========================================================================= */
function el(tag, cls = '', text = '') {
  const node = document.createElement(tag);
  if (cls) node.className = cls;
  if (text) node.textContent = text;
  return node;
}

function formatDateTime(iso) {
  if (!iso) return '';
  const d = new Date(iso);
  const dateOpts = { day: '2-digit', month: 'long', year: 'numeric' };
  const timeOpts = { hour: '2-digit', minute: '2-digit' };
  return `${d.toLocaleDateString('pl-PL', dateOpts)}, ${d.toLocaleTimeString('pl-PL', timeOpts)}`;
}

function markAsRead(id, element) {
  fetch(`/notifications/markAsRead/${id}`, {
    method: 'POST',
    headers: {
      [CSRF_HEADER_]: CSRF_TOKEN_
    }
  })
  .then(res => {
    if (!res.ok) throw new Error(`Status ${res.status}`);
    element.remove();
    const badge = document.querySelector('#alertsDropdown .badge-counter');
    const current = parseInt(badge.textContent) || 0;
    badge.textContent = (current - 1) > 0 ? (current - 1) : '';
  })
  .catch(err => console.error('Błąd oznaczania jako przeczytane:', err));
}


/* =========================================================================
   2.  RENDEROWANIE POWIADOMIENIA Z KRZYŻYKIEM
   ========================================================================= */
function renderNotification(n) {
  const a = el('a', 'dropdown-item d-flex align-items-center position-relative notification-item');
  a.href = '#';

  // przycisk zamknięcia (×)
  const closeBtn = el('span', 'notification-close');
  closeBtn.textContent = '×';
  closeBtn.title = 'Oznacz jako przeczytane';
  // styl: pozycja absolute w prawym górnym rogu
  closeBtn.style.cssText = 'position:absolute;top:8px;right:12px;cursor:pointer;color:#e74a3b;font-weight:bold;';
  closeBtn.addEventListener('click', e => {
    e.stopPropagation();
    markAsRead(n.id, a);
  });
  a.appendChild(closeBtn);

  // ikona
  const mr3 = el('div', 'mr-3');
  const iconCircle = el('div', 'icon-circle bg-info');
  const icon = el('i', 'fas fa-bell text-white');
  iconCircle.appendChild(icon);
  mr3.appendChild(iconCircle);
  a.appendChild(mr3);

  // content + data
  const body = el('div');
  const small = el('div', 'small text-gray-500', formatDateTime(n.createdAt));
  const span = el('span', 'font-weight-bold', n.content);
  body.appendChild(small);
  body.appendChild(span);
  a.appendChild(body);

  return a;
}

/* =========================================================================
   3.  ŁADOWANIE I RENDEROWANIE LISTY
   ========================================================================= */
function loadNotifications() {
  const senderId = document.body.dataset.senderId;
  if (!senderId) return;

  const dropdown = document.querySelector('#alertsDropdown').nextElementSibling;
  const badge    = document.querySelector('#alertsDropdown .badge-counter');

  fetch(`/notifications/${senderId}`)
    .then(res => {
      if (!res.ok) throw new Error(res.status);
      return res.json();
    })
    .then(list => {
      const unread = list.filter(n => !n.read);
      badge.textContent = unread.length > 0 ? unread.length : '';

      const header = dropdown.querySelector('.dropdown-header');
      const footer = dropdown.querySelector('.dropdown-item.text-center');
      dropdown.innerHTML = '';
      dropdown.appendChild(header);

      if (unread.length === 0) {
        const empty = el('span', 'dropdown-item text-gray-500 small text-center', 'Brak nowych powiadomień');
        dropdown.appendChild(empty);
      } else {
        unread.forEach(n => dropdown.appendChild(renderNotification(n)));
      }

      dropdown.appendChild(footer);
    })
    .catch(err => console.error('Błąd podczas pobierania powiadomień:', err));
}



/* =========================================================================
   5.  WSZYSTKIE POWIADOMIENIA W MODALU
   ========================================================================= */
function loadAllNotifications() {
  const senderId = document.body.dataset.senderId;
  if (!senderId) return;

  fetch(`/notifications/${senderId}`)
    .then(res => {
      if (!res.ok) throw new Error(res.status);
      return res.json();
    })
    .then(list => {
      const container = document.getElementById('allNotificationsBody');
      container.innerHTML = '';

      if (!list.length) {
        container.textContent = 'Brak powiadomień.';
      } else {
        list.forEach(n => {
          // prosty div zamiast linku
          const item = el('div', 'd-flex align-items-center mb-2 p-2 rounded ' + (n.read ? 'bg-light' : 'bg-white'));
          
          // ikona
          const icon = el('i', 'fas fa-bell fa-lg text-info mr-3');
          item.appendChild(icon);

          // treść i data
          const text = el('div', '', '');
          const small = el('div', 'small text-gray-500', formatDateTime(n.createdAt));
          const span  = el('div', 'font-weight-bold', n.content);
          text.appendChild(small);
          text.appendChild(span);
          item.appendChild(text);

          container.appendChild(item);
        });
      }

      // pokaż modal (Bootstrap JS)
      $('#allNotificationsModal').modal('show');
    })
    .catch(err => console.error('Błąd podczas ładowania wszystkich powiadomień:', err));
}

/* =========================================================================
   5.  START
   ========================================================================= */
document.addEventListener('DOMContentLoaded', () => {
  loadNotifications();
  setInterval(loadNotifications, 15000);
});

document.addEventListener('DOMContentLoaded', () => {
  const showAll = document.getElementById('showAllNotifications');
  if (showAll) {
    showAll.addEventListener('click', e => {
      e.preventDefault();
      loadAllNotifications();
    });
  }
});


