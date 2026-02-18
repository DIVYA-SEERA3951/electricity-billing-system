/* ======================================================
   PowerBill — app.js
   Shared utilities, API helpers, session management
   ====================================================== */

/* ---- Alert helper ---- */
function showAlert(message, type = 'success') {
  let box = document.getElementById('alertBox');
  if (!box) { box = document.createElement('div'); box.id = 'alertBox'; document.body.appendChild(box); }
  box.innerHTML = `
    <div class="alert alert-${type} alert-dismissible fade show shadow" role="alert">
      ${message}
      <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>`;
  setTimeout(() => { if (box) box.innerHTML = ''; }, 5000);
}

/* ---- Generic fetch wrapper ---- */
async function apiFetch(url, method = 'GET', body = null) {
  const opts = { method, headers: { 'Content-Type': 'application/json' }, credentials: 'same-origin' };
  if (body) opts.body = JSON.stringify(body);
  const res  = await fetch(url, opts);
  const data = await res.json();
  if (!res.ok) {
    const msg = data.message || data.error || 'An error occurred';
    throw new Error(msg);
  }
  return data;
}

/* ---- Session check (call at top of every protected page) ----
   expectedRole: 'ADMIN' | 'CUSTOMER' | null (any logged-in user)
   Returns session data on success, redirects on failure.
*/
async function checkSession(expectedRole) {
  try {
    const data = await apiFetch('/api/auth/check');
    if (expectedRole && data.role !== expectedRole) {
      // Wrong role — send them to the right dashboard
      if (data.role === 'ADMIN')    window.location.href = 'admin-dashboard.html';
      if (data.role === 'CUSTOMER') window.location.href = 'customer-dashboard.html';
      return null;
    }
    return data;
  } catch (e) {
    window.location.href = 'login.html';
    return null;
  }
}

/* ---- Logout ---- */
async function logout() {
  try { await apiFetch('/api/logout', 'POST'); } catch (_) {}
  window.location.href = 'login.html';
}

/* ---- Set navbar username ---- */
function setNavUser(username, role) {
  const el = document.getElementById('navUser');
  if (!el) return;
  const badge = role === 'ADMIN'
    ? `<span class="badge-admin">${role}</span>`
    : `<span class="badge-customer">${role}</span>`;
  el.innerHTML = `👤 ${username} &nbsp;${badge}`;
}

/* ====================================================
   ADMIN FUNCTIONS
   ==================================================== */

async function adminLoadCustomers() {
  try {
    const list = await apiFetch('/api/admin/customers');
    renderAdminCustomers(list);
    // Populate dropdown on bills page
    const sel = document.getElementById('billCustomerSelect');
    if (sel) {
      sel.innerHTML = '<option value="">— Select customer —</option>';
      list.forEach(c => { sel.innerHTML += `<option value="${c.id}">${c.name} (ID: ${c.id})</option>`; });
    }
    return list;
  } catch (e) { showAlert('Failed to load customers: ' + e.message, 'danger'); return []; }
}

function renderAdminCustomers(list) {
  const tb = document.getElementById('customersTableBody');
  if (!tb) return;
  if (!list.length) { tb.innerHTML = '<tr><td colspan="5" class="text-center text-muted py-3">No customers found</td></tr>'; return; }
  tb.innerHTML = list.map(c => `
    <tr>
      <td>${c.id}</td>
      <td>${c.name}</td>
      <td>${c.email}</td>
      <td>${c.address}</td>
      <td><button class="btn btn-danger btn-sm" onclick="adminDeleteCustomer(${c.id})">🗑 Delete</button></td>
    </tr>`).join('');
}

async function adminAddCustomer(e) {
  e.preventDefault();
  const name    = document.getElementById('custName').value.trim();
  const email   = document.getElementById('custEmail').value.trim();
  const address = document.getElementById('custAddress').value.trim();
  try {
    await apiFetch('/api/admin/customers', 'POST', { name, email, address });
    showAlert('Customer added!', 'success');
    document.getElementById('customerForm').reset();
    await adminLoadCustomers();
  } catch (e) { showAlert('Error: ' + e.message, 'danger'); }
}

async function adminDeleteCustomer(id) {
  if (!confirm('Delete this customer and all their bills?')) return;
  try {
    await apiFetch('/api/admin/customers/' + id, 'DELETE');
    showAlert('Customer deleted.', 'success');
    await adminLoadCustomers();
  } catch (e) { showAlert('Error: ' + e.message, 'danger'); }
}

async function adminLoadBills() {
  try {
    const list = await apiFetch('/api/admin/bills');
    renderAdminBills(list);
    return list;
  } catch (e) { showAlert('Failed to load bills: ' + e.message, 'danger'); return []; }
}

function renderAdminBills(list) {
  const tb = document.getElementById('billsTableBody');
  if (!tb) return;
  if (!list.length) { tb.innerHTML = '<tr><td colspan="6" class="text-center text-muted py-3">No bills found</td></tr>'; return; }
  tb.innerHTML = list.map(b => `
    <tr>
      <td>${b.id}</td>
      <td>${b.customer ? b.customer.name : 'N/A'}</td>
      <td>${b.unitsConsumed}</td>
      <td>₹${b.amount.toFixed(2)}</td>
      <td>${b.billDate}</td>
      <td><span class="badge bg-success">Generated</span></td>
    </tr>`).join('');
}

async function adminGenerateBill(e) {
  e.preventDefault();
  const customerId    = document.getElementById('billCustomerSelect') 
      ? document.getElementById('billCustomerSelect').value 
      : document.getElementById('billCustomerId').value;
  const unitsConsumed = parseFloat(document.getElementById('billUnits').value);
  if (!customerId) { showAlert('Please select a customer', 'warning'); return; }
  try {
    await apiFetch('/api/admin/bills', 'POST', { customerId: Number(customerId), unitsConsumed });
    showAlert('Bill generated!', 'success');
    document.getElementById('billForm').reset();
    await adminLoadBills();
  } catch (e) { showAlert('Error: ' + e.message, 'danger'); }
}

/* ====================================================
   ADMIN DASHBOARD STATS
   ==================================================== */
async function adminLoadStats() {
  try {
    const [customers, bills] = await Promise.all([
      apiFetch('/api/admin/customers'),
      apiFetch('/api/admin/bills')
    ]);
    const revenue = bills.reduce((s, b) => s + b.amount, 0);
    const elC = document.getElementById('statCustomers');
    const elB = document.getElementById('statBills');
    const elR = document.getElementById('statRevenue');
    if (elC) elC.textContent = customers.length;
    if (elB) elB.textContent = bills.length;
    if (elR) elR.textContent = '₹' + revenue.toFixed(2);
  } catch (e) { console.error(e); }
}

/* ====================================================
   CUSTOMER FUNCTIONS
   ==================================================== */
async function customerLoadProfile() {
  try {
    const p  = await apiFetch('/api/customer/profile');
    const el = document.getElementById('profileCard');
    if (!el) return;
    el.innerHTML = `
      <div class="row">
        <div class="col-6"><strong>Name:</strong></div>      <div class="col-6">${p.name}</div>
        <div class="col-6 mt-2"><strong>Email:</strong></div><div class="col-6 mt-2">${p.email}</div>
        <div class="col-6 mt-2"><strong>Address:</strong></div><div class="col-6 mt-2">${p.address}</div>
        <div class="col-6 mt-2"><strong>Customer ID:</strong></div><div class="col-6 mt-2">${p.id}</div>
      </div>`;
  } catch (e) { showAlert('Failed to load profile: ' + e.message, 'danger'); }
}

async function customerLoadBills() {
  try {
    const list = await apiFetch('/api/customer/bills');
    const tb   = document.getElementById('myBillsTableBody');
    if (!tb) return;
    if (!list.length) { tb.innerHTML = '<tr><td colspan="5" class="text-center text-muted py-3">No bills yet</td></tr>'; return; }
    tb.innerHTML = list.map(b => `
      <tr>
        <td>${b.id}</td>
        <td>${b.unitsConsumed}</td>
        <td>₹${b.amount.toFixed(2)}</td>
        <td>${b.billDate}</td>
        <td><span class="badge bg-success">Generated</span></td>
      </tr>`).join('');
    // Stats card
    const total = list.reduce((s, b) => s + b.amount, 0);
    const elB = document.getElementById('statMyBills');
    const elA = document.getElementById('statMyAmount');
    if (elB) elB.textContent = list.length;
    if (elA) elA.textContent = '₹' + total.toFixed(2);
  } catch (e) { showAlert('Failed to load bills: ' + e.message, 'danger'); }
}
