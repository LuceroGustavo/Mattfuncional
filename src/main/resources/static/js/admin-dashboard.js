// ===== ADMIN DASHBOARD JAVASCRIPT =====

class AdminDashboard {
    constructor() {
        this.init();
    }

    init() {
        this.setupEventListeners();
        this.setupAnimations();
        this.setupNotifications();
        this.setupConfirmations();
        this.setupLoadingStates();
    }

    // ===== EVENT LISTENERS =====
    setupEventListeners() {
        // Confirmación para acciones destructivas
        document.querySelectorAll('[data-confirm]').forEach(element => {
            element.addEventListener('click', (e) => {
                const message = element.getAttribute('data-confirm');
                if (!this.showConfirmation(message)) {
                    e.preventDefault();
                    return false;
                }
            });
        });

        // Tooltips para botones
        document.querySelectorAll('[data-tooltip]').forEach(element => {
            element.addEventListener('mouseenter', (e) => {
                this.showTooltip(e.target, e.target.getAttribute('data-tooltip'));
            });
            
            element.addEventListener('mouseleave', (e) => {
                this.hideTooltip();
            });
        });

        // Formularios con validación
        document.querySelectorAll('form').forEach(form => {
            form.addEventListener('submit', (e) => {
                if (!this.validateForm(form)) {
                    e.preventDefault();
                    return false;
                }
                this.showLoading(form);
            });
        });

        // Tablas con ordenamiento
        document.querySelectorAll('.admin-table th[data-sort]').forEach(header => {
            header.addEventListener('click', (e) => {
                this.sortTable(header);
            });
        });

        // Búsqueda en tiempo real
        const searchInput = document.getElementById('search-input');
        if (searchInput) {
            searchInput.addEventListener('input', (e) => {
                this.filterTable(e.target.value);
            });
        }
    }

    // ===== ANIMATIONS =====
    setupAnimations() {
        // Animación de entrada para las tarjetas
        const observer = new IntersectionObserver((entries) => {
            entries.forEach((entry, index) => {
                if (entry.isIntersecting) {
                    entry.target.style.setProperty('--animation-order', index);
                    entry.target.classList.add('animate-in');
                }
            });
        }, { threshold: 0.1 });

        document.querySelectorAll('.stat-card, .action-card').forEach(card => {
            observer.observe(card);
        });

        // Animación de hover para botones
        document.querySelectorAll('.action-btn, .btn-edit, .btn-delete, .btn-view').forEach(btn => {
            btn.addEventListener('mouseenter', (e) => {
                e.target.style.transform = 'translateY(-2px) scale(1.02)';
            });
            
            btn.addEventListener('mouseleave', (e) => {
                e.target.style.transform = 'translateY(0) scale(1)';
            });
        });
    }

    // ===== NOTIFICATIONS =====
    setupNotifications() {
        // Mostrar notificaciones de éxito/error desde URL params
        const urlParams = new URLSearchParams(window.location.search);
        const success = urlParams.get('success');
        const error = urlParams.get('error');

        if (success) {
            this.showNotification(this.getSuccessMessage(success), 'success');
        }

        if (error) {
            this.showNotification(this.getErrorMessage(error), 'error');
        }
    }

    getSuccessMessage(key) {
        const messages = {
            'profesor_creado': 'Profesor creado exitosamente',
            'profesor_actualizado': 'Profesor actualizado exitosamente',
            'profesor_eliminado': 'Profesor eliminado exitosamente',
            'alumno_creado': 'Alumno creado exitosamente',
            'alumno_actualizado': 'Alumno actualizado exitosamente',
            'alumno_eliminado': 'Alumno eliminado exitosamente',
            'ejercicio_creado': 'Ejercicio creado exitosamente',
            'ejercicio_actualizado': 'Ejercicio actualizado exitosamente',
            'ejercicio_eliminado': 'Ejercicio eliminado exitosamente',
            'usuario_actualizado': 'Usuario actualizado exitosamente',
            'usuario_eliminado': 'Usuario eliminado exitosamente'
        };
        return messages[key] || 'Operación completada exitosamente';
    }

    getErrorMessage(key) {
        const messages = {
            'profesor_no_encontrado': 'Profesor no encontrado',
            'alumno_no_encontrado': 'Alumno no encontrado',
            'ejercicio_no_encontrado': 'Ejercicio no encontrado',
            'usuario_no_encontrado': 'Usuario no encontrado',
            'form_error': 'Error en el formulario',
            'dashboard_error': 'Error al cargar el dashboard',
            'ejercicios_error': 'Error al cargar ejercicios',
            'error_eliminacion': 'Error al eliminar el elemento',
            'profesor_admin_no_encontrado': 'No se encontró el profesor administrador'
        };
        return messages[key] || 'Ha ocurrido un error';
    }

    showNotification(message, type = 'info') {
        const notification = document.createElement('div');
        notification.className = `alert alert-${type === 'error' ? 'danger' : type} notification`;
        notification.innerHTML = `
            <div class="d-flex align-items-center">
                <i class="fas fa-${type === 'success' ? 'check-circle' : type === 'error' ? 'exclamation-circle' : 'info-circle'} me-2"></i>
                <span>${message}</span>
                <button type="button" class="btn-close ms-auto" onclick="this.parentElement.parentElement.remove()"></button>
            </div>
        `;

        // Insertar al inicio del dashboard
        const dashboard = document.querySelector('.admin-dashboard');
        if (dashboard) {
            dashboard.insertBefore(notification, dashboard.firstChild);
        }

        // Auto-remover después de 5 segundos
        setTimeout(() => {
            if (notification.parentElement) {
                notification.remove();
            }
        }, 5000);
    }

    // ===== CONFIRMATIONS =====
    setupConfirmations() {
        // Confirmación personalizada para eliminaciones
        document.querySelectorAll('.btn-delete').forEach(btn => {
            btn.addEventListener('click', (e) => {
                const message = btn.getAttribute('data-confirm') || '¿Estás seguro de que quieres eliminar este elemento?';
                if (!this.showConfirmation(message)) {
                    e.preventDefault();
                    return false;
                }
            });
        });
    }

    showConfirmation(message) {
        return confirm(message);
    }

    // ===== LOADING STATES =====
    setupLoadingStates() {
        // Mostrar loading en botones durante acciones
        document.querySelectorAll('.action-btn, .btn-primary').forEach(btn => {
            btn.addEventListener('click', (e) => {
                if (!btn.classList.contains('loading')) {
                    this.showButtonLoading(btn);
                }
            });
        });
    }

    showButtonLoading(button) {
        const originalText = button.innerHTML;
        button.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Cargando...';
        button.classList.add('loading');
        button.disabled = true;

        // Restaurar después de un tiempo (o cuando la acción termine)
        setTimeout(() => {
            button.innerHTML = originalText;
            button.classList.remove('loading');
            button.disabled = false;
        }, 2000);
    }

    showLoading(element) {
        element.classList.add('loading');
        element.style.pointerEvents = 'none';
    }

    // ===== FORM VALIDATION =====
    validateForm(form) {
        let isValid = true;
        const requiredFields = form.querySelectorAll('[required]');

        requiredFields.forEach(field => {
            if (!field.value.trim()) {
                this.showFieldError(field, 'Este campo es obligatorio');
                isValid = false;
            } else {
                this.clearFieldError(field);
            }
        });

        // Validación de email
        const emailFields = form.querySelectorAll('input[type="email"]');
        emailFields.forEach(field => {
            if (field.value && !this.isValidEmail(field.value)) {
                this.showFieldError(field, 'Ingrese un email válido');
                isValid = false;
            }
        });

        return isValid;
    }

    isValidEmail(email) {
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return emailRegex.test(email);
    }

    showFieldError(field, message) {
        field.classList.add('is-invalid');
        let errorDiv = field.parentElement.querySelector('.invalid-feedback');
        if (!errorDiv) {
            errorDiv = document.createElement('div');
            errorDiv.className = 'invalid-feedback';
            field.parentElement.appendChild(errorDiv);
        }
        errorDiv.textContent = message;
    }

    clearFieldError(field) {
        field.classList.remove('is-invalid');
        const errorDiv = field.parentElement.querySelector('.invalid-feedback');
        if (errorDiv) {
            errorDiv.remove();
        }
    }

    // ===== TABLE SORTING =====
    sortTable(header) {
        const table = header.closest('table');
        const tbody = table.querySelector('tbody');
        const rows = Array.from(tbody.querySelectorAll('tr'));
        const columnIndex = Array.from(header.parentElement.children).indexOf(header);
        const isAscending = !header.classList.contains('sort-asc');

        // Limpiar clases de ordenamiento
        table.querySelectorAll('th').forEach(th => {
            th.classList.remove('sort-asc', 'sort-desc');
        });

        // Agregar clase de ordenamiento
        header.classList.add(isAscending ? 'sort-asc' : 'sort-desc');

        // Ordenar filas
        rows.sort((a, b) => {
            const aValue = a.children[columnIndex].textContent.trim();
            const bValue = b.children[columnIndex].textContent.trim();
            
            if (isAscending) {
                return aValue.localeCompare(bValue);
            } else {
                return bValue.localeCompare(aValue);
            }
        });

        // Reinsertar filas ordenadas
        rows.forEach(row => tbody.appendChild(row));
    }

    // ===== TABLE FILTERING =====
    filterTable(searchTerm) {
        const table = document.querySelector('.admin-table');
        if (!table) return;

        const rows = table.querySelectorAll('tbody tr');
        const searchLower = searchTerm.toLowerCase();

        rows.forEach(row => {
            const text = row.textContent.toLowerCase();
            if (text.includes(searchLower)) {
                row.style.display = '';
            } else {
                row.style.display = 'none';
            }
        });
    }

    // ===== TOOLTIPS =====
    showTooltip(element, text) {
        const tooltip = document.createElement('div');
        tooltip.className = 'tooltip';
        tooltip.textContent = text;
        tooltip.style.cssText = `
            position: absolute;
            background: #333;
            color: white;
            padding: 5px 10px;
            border-radius: 4px;
            font-size: 12px;
            z-index: 1000;
            pointer-events: none;
        `;

        document.body.appendChild(tooltip);

        const rect = element.getBoundingClientRect();
        tooltip.style.left = rect.left + (rect.width / 2) - (tooltip.offsetWidth / 2) + 'px';
        tooltip.style.top = rect.top - tooltip.offsetHeight - 5 + 'px';

        element.tooltip = tooltip;
    }

    hideTooltip() {
        const tooltip = document.querySelector('.tooltip');
        if (tooltip) {
            tooltip.remove();
        }
    }

    // ===== UTILITY METHODS =====
    debounce(func, wait) {
        let timeout;
        return function executedFunction(...args) {
            const later = () => {
                clearTimeout(timeout);
                func(...args);
            };
            clearTimeout(timeout);
            timeout = setTimeout(later, wait);
        };
    }

    // ===== AJAX HELPERS =====
    async makeRequest(url, options = {}) {
        try {
            const response = await fetch(url, {
                headers: {
                    'Content-Type': 'application/json',
                    ...options.headers
                },
                ...options
            });

            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            return await response.json();
        } catch (error) {
            console.error('Request failed:', error);
            this.showNotification('Error en la petición: ' + error.message, 'error');
            throw error;
        }
    }

    // ===== STATISTICS UPDATES =====
    updateStatistics() {
        // Actualizar estadísticas en tiempo real si es necesario
        const statCards = document.querySelectorAll('.stat-card');
        statCards.forEach(card => {
            const numberElement = card.querySelector('.stat-number');
            if (numberElement) {
                const currentValue = parseInt(numberElement.textContent);
                this.animateNumber(numberElement, currentValue, currentValue + Math.floor(Math.random() * 10));
            }
        });
    }

    animateNumber(element, start, end) {
        const duration = 1000;
        const startTime = performance.now();
        
        const animate = (currentTime) => {
            const elapsed = currentTime - startTime;
            const progress = Math.min(elapsed / duration, 1);
            
            const current = Math.floor(start + (end - start) * progress);
            element.textContent = current;
            
            if (progress < 1) {
                requestAnimationFrame(animate);
            }
        };
        
        requestAnimationFrame(animate);
    }
}

// ===== FUNCIONES GLOBALES =====

// Función para cargar ejercicios predeterminados
async function cargarEjerciciosPredeterminados() {
    try {
        const response = await fetch('/admin/ejercicios/cargar-predeterminados', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            }
        });
        
        const result = await response.json();
        
        if (response.ok) {
            // Mostrar mensaje de éxito con la cantidad de ejercicios
            const mensaje = result.ejerciciosCargados ? 
                `¡Éxito! Se cargaron ${result.ejerciciosCargados} ejercicios predeterminados.` : 
                result.message;
            
            showNotification(mensaje, 'success');
            
            // Cerrar el modal automáticamente si existe
            const modal = document.getElementById('confirmarCargarEjerciciosModal');
            if (modal) {
                const bootstrapModal = bootstrap.Modal.getInstance(modal);
                if (bootstrapModal) {
                    bootstrapModal.hide();
                }
            }
            
            // Recargar la página después de un breve delay
            setTimeout(() => {
                window.location.reload();
            }, 2000);
        } else {
            showNotification('Error al cargar ejercicios: ' + result.message, 'error');
        }
    } catch (error) {
        showNotification('Error de conexión: ' + error.message, 'error');
    }
}

// Función para limpiar datos inconsistentes
async function limpiarDatosInconsistentes() {
    if (!confirm('¿Está seguro que desea limpiar los datos inconsistentes? Esta acción puede tomar varios segundos.')) {
        return;
    }
    
    try {
        const button = event.target;
        const originalText = button.innerHTML;
        button.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Limpiando...';
        button.disabled = true;
        
        const response = await fetch('/admin/limpiar-datos-inconsistentes', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            }
        });
        
        const result = await response.json();
        
        if (response.ok) {
            let message = 'Limpieza completada:\n';
            if (result.usuariosConRolCorregido > 0) {
                message += `- ${result.usuariosConRolCorregido} usuarios con rol corregido\n`;
            }
            if (result.avataresAsignados) {
                message += '- Avatares asignados\n';
            }
            if (result.profesoresSinUsuario > 0) {
                message += `- ${result.profesoresSinUsuario} profesores sin usuario asociado\n`;
            }
            
            alert(message);
            setTimeout(() => {
                window.location.reload();
            }, 2000);
        } else {
            showNotification('Error en la limpieza: ' + result.error, 'error');
        }
    } catch (error) {
        showNotification('Error de conexión: ' + error.message, 'error');
    } finally {
        const button = event.target;
        button.innerHTML = '<i class="fas fa-wrench"></i> Limpiar Datos';
        button.disabled = false;
    }
}





// Función global para mostrar notificaciones
function showNotification(message, type = 'info') {
    const notification = document.createElement('div');
    notification.className = `alert alert-${type === 'error' ? 'danger' : type} alert-dismissible fade show position-fixed`;
    notification.style.cssText = 'top: 20px; right: 20px; z-index: 9999; min-width: 300px;';
    notification.innerHTML = `
        ${message}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    `;
    
    document.body.appendChild(notification);
    
    setTimeout(() => {
        if (notification.parentNode) {
            notification.remove();
        }
    }, 5000);
}

// Inicializar el dashboard cuando el DOM esté listo
document.addEventListener('DOMContentLoaded', () => {
    new AdminDashboard();
}); 