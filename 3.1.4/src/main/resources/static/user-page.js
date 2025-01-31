function loadUser() {
    fetch('/api/user/my')
        .then(response => {
            if (!response.ok) {
                throw new Error('Сетевая ошибка');
            }
            return response.json();
        })
        .then(user => {
            const navUserInfo = document.getElementById('nav-user-info');

            const userRoles = user.role.map(role => role.name.substring(5)).join(', ');
            navUserInfo.textContent = `${user.name} with roles: ${userRoles}`;

            const tableBody = document.getElementById('user-table-body');
            tableBody.innerHTML = '';

            const row = document.createElement('tr');
            row.innerHTML = `
                <td>${user.id}</td>
                <td>${user.name}</td>
                <td>${user.surname}</td>
                <td>${user.age}</td>
                <td>${user.email}</td>
                <td>${userRoles}</td>
            `;
            tableBody.appendChild(row);
        })
        .catch(error => console.error('Ошибка при загрузке пользователя:', error));
}

document.getElementById('logout-button').addEventListener('click', function() {
    fetch('/logout', {
        method: 'POST',
    })
        .then(response => {
            if (response.ok) {
                window.location.href = '/login';
            } else {
                console.error('Ошибка при выходе из системы:', response.status);
            }
        })
        .catch(error => console.error('Ошибка при выходе из системы:', error));
});

document.getElementById('load-users-button').addEventListener('click', loadUser);
document.addEventListener('DOMContentLoaded', loadUser);
