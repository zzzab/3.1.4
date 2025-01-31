document.addEventListener('DOMContentLoaded', function() {
    showSection('admin-panel');
    loadAllUsers();
    loadUser();
    toggleTables();
});


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

            const tableBody = document.getElementById('user-table');
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

function loadAllUsers() {
    fetch('/api/users')
        .then(response => response.json())
        .then(data => {
            const allUsersTable = document.getElementById('all-users-table');
            allUsersTable.innerHTML = '';

            data.forEach(user => {
                const usersRoles = user.role.map(role => role.name.substring(5)).join(', ');

                allUsersTable.innerHTML += `
                    <tr>
                        <td>${user.id}</td>
                        <td>${user.name}</td>
                        <td>${user.surname}</td>
                        <td>${user.age}</td>
                        <td>${user.email}</td>
                        <td>${usersRoles}</td>
                        <td>
                           <div class="button-container">
                            <button onclick="editUser(${user.id})">Edit</button>
                            <button onclick="deleteUser(${user.id})">Delete</button>
                            </div>
                        </td>
                    </tr>
                `;
            });
        });
}

function addUser() {
    const selectedRoles = Array.from(document.getElementById('rolesSelect1').selectedOptions)
        .map(option => ({ name: option.value }));

    const userObject = {
        username: document.getElementById('inputFirstName1').value,
        surname: document.getElementById('inputLastName1').value,
        age: document.getElementById('inputAge1').value,
        email: document.getElementById('inputEmail1').value,
        password: document.getElementById('inputPassword1').value,
        roles: selectedRoles
    };

    console.log(JSON.stringify(userObject));

    fetch('/api/users', {
        method: 'POST',
        body: JSON.stringify(userObject),
        headers: {
            'Content-Type': 'application/json'
        }
    }).then(response => {
        if (response.ok) {
            return response.json().then(data => {
                console.log('Пользователь успешно добавлен:', data);
                loadAllUsers();
                window.location.href = '/admin/users';
            });
        } else {
            return response.json().then(error => {
                throw new Error(error.message);
            });
        }
    }).catch(error => {
        console.error('Ошибка при добавлении пользователя:', error);
    });
}

function editUser(userId) {
    fetch(`/api/users/${userId}`)
        .then(response => {
            if (!response.ok) {
                throw new Error('Ошибка загрузки пользователя');
            }
            return response.json();
        })
        .then(user => {

            document.getElementById('inputUserId').value = user.id;
            document.getElementById('inputFirstName').value = user.name;
            document.getElementById('inputLastName').value = user.surname;
            document.getElementById('inputAge').value = user.age;
            document.getElementById('inputEmail').value = user.email;
            // Устанавливаем роли
            const rolesSelect = document.getElementById('rolesSelect1');

            const userRoles = ['ROLE_USER', 'ROLE_ADMIN'];

            for (let option of rolesSelect.options) {
                option.selected = userRoles.includes(option.value);
            }
            document.getElementById('edit-user-modal').classList.remove('hidden');
        })
        .catch(error => console.error('Ошибка при редактировании пользователя:', error));
}

function saveChanges() {
    const userId = document.getElementById('inputUserId').value;
    const firstName = document.getElementById('inputFirstName').value;
    const lastName = document.getElementById('inputLastName').value;
    const age = document.getElementById('inputAge').value;
    const email = document.getElementById('inputEmail').value;
    const password = document.getElementById('inputPassword').value;
    const rolesSelect = document.getElementById('rolesSelect');
    const roles = Array.from(rolesSelect.selectedOptions).map(option => ({ name: option.value }));

    const updatedUser = {
        id: userId,
        name: firstName,
        surname: lastName,
        age: age,
        email: email,
        password : password,
        roles: roles
    };

    fetch(`/api/users/${userId}`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(updatedUser),
    })
        .then(response => {
            if (response.ok) {
                closeModal('edit-user-modal');
                loadAllUsers();

            } else {
                return response.json().then(error => {
                    throw new Error(error.message);
                });
            }
        })
        .catch(error => {
            alert(`Ошибка при обновлении пользователя: ${error.message}`);
        });
}

let userIdToDelete;

function deleteUser(userId) {
    userIdToDelete = userId;

    fetch(`/api/users/${userId}`, {
    }).then(response => {
        if (!response.ok) {
            throw new Error('Ошибка загрузки пользователя');
        }
        return response.json();
    })
        .then(user => {
            document.getElementById('inputUserId2').value = user.id;
            document.getElementById('inputFirstName2').value = user.name;
            document.getElementById('inputLastName2').value = user.surname;
            document.getElementById('inputAge2').value = user.age;
            document.getElementById('inputEmail2').value = user.email;
            document.getElementById('delete-user-modal').classList.remove('hidden');
        }).catch(error => console.error('Ошибка при удалении пользователя:', error));
}
function deletindUser() {
    const userId = document.getElementById('inputUserId2').value;

    fetch(`/api/users/${userId}`, {
        method: 'DELETE',
        headers: {
            'Content-Type': 'application/json',
        },
    })
        .then(response => {
            if (response.ok) {
                closeModal('delete-user-modal');
                loadAllUsers();

            }
            throw new Error('Не удалось удалить пользователя');
        })
        .then(data => {
            console.log(data.message);
        })
        .catch(error => {
            console.error('Ошибка:', error);
        });
}




document.addEventListener('DOMContentLoaded', function() {
    const allUsersButton = document.getElementById('btnRadio1');
    const newUserButton = document.getElementById('btnRadio2');

    if (allUsersButton && newUserButton) {
        allUsersButton.addEventListener('change', toggleTables);
        newUserButton.addEventListener('change', toggleTables);
    } else {
        console.error('Кнопки не найдены');
    }

    // Вызовем toggleTables, чтобы показать нужный элемент по умолчанию
    toggleTables();
});

function toggleTables() {
    const allUsersTable = document.getElementById('table1');
    const newUserForm = document.getElementById('table2');

    if (allUsersTable && newUserForm) {
        if (document.getElementById('btnRadio1').checked) {
            allUsersTable.classList.remove('hidden');
            newUserForm.classList.add('hidden');
        } else {
            allUsersTable.classList.add('hidden');
            newUserForm.classList.remove('hidden');
        }
    } else {
        console.error('Таблицы не найдены');
    }
}

function closeModal(modalId) {
    const modal = document.getElementById(modalId);
    if (modal) {
        modal.classList.add('hidden');
    }
}

// Here we click the user button and load user's initial data
document.getElementById('load-users-button').addEventListener('click', function() {
    showSection('user-info');
    loadUser();
});

// And here we click the admin button and load all users' data
document.getElementById('load-all-users-button').addEventListener('click', function() {
    showSection('admin-panel');
    loadAllUsers();
});

function showSection(sectionId) {
    const sections = document.querySelectorAll('.scrollspy-example-2');
    sections.forEach(section => {
        section.classList.add('hidden');
    });
    const activeSection = document.getElementById(sectionId);
    if (activeSection) {
        activeSection.classList.remove('hidden');
    }
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



