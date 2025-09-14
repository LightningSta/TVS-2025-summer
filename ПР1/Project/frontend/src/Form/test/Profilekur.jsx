import { useEffect, useState } from 'react'; 
import { useNavigate } from 'react-router-dom';
import axios from 'axios';

export default function Profilekur() {
  const [user, setUser] = useState(null);
  const [position, setPosition] = useState('');
  const [priority, setPriority] = useState(2);
  const [usersWithPriority, setUsersWithPriority] = useState([]);
  const [error, setError] = useState('');
  const navigate = useNavigate();

  useEffect(() => {
    const token = localStorage.getItem('token');

    if (!token) {
      navigate('/login');
    } else {
      axios
        .get('http://localhost:5000/api/profilekur', {
          headers: { Authorization: `Bearer ${token}` },
        })
        .then((response) => {
          const userData = response.data.user;
          setUser(userData);
          setPosition(userData.position);
          setPriority(userData.priority);

          if (userData.priority === 1) {
            const users = response.data.usersWithPosition;
            setUsersWithPriority(users);
          }
        })
        .catch((error) => {
          console.error('Error fetching profile', error);
          setError('Ошибка при загрузке данных');
        });
    }
  }, [navigate]);

  const handlePositionChange = (e) => {
    setPosition(e.target.value);
  };

  const handleSave = async () => {
    const token = localStorage.getItem('token');
    try {
      // Сделаем запрос к правильному маршруту для обновления должности
      const response = await axios.put(
        `http://localhost:5000/api/users/${user.id}/position`,  // правильный маршрут
        { position },
        {
          headers: { Authorization: `Bearer ${token}` },
        }
      );
      setUser(response.data);  // Обновим данные пользователя
      setError('');
    } catch (error) {
      console.error('Error saving position', error);
      setError('Ошибка при сохранении данных');
    }
  };
  

  const handleChangePositionForUser = async (userId, newPosition) => {
    const token = localStorage.getItem('token');
    try {
      const response = await axios.put(
        `http://localhost:5000/api/users/${userId}/position`,  // исправил шаблонную строку
        { position: newPosition },
        {
          headers: { Authorization: `Bearer ${token}` },
        }
      );
      setUsersWithPriority((prev) =>
        prev.map((user) =>
          user.id === userId ? { ...user, position: response.data.position } : user
        )
      );
    } catch (error) {
      setError('Ошибка при изменении должности пользователя');
    }
  };

  if (!user) {
    return <div>Загрузка...</div>;
  }

  return (
    <div>
      <h1>Добро пожаловать, {user.login}</h1>
      <p>Email: {user.email}</p>
      <p>Должность: {user.position}</p>

      {user.priority === 1 && (
        <div>
          <h2>Пользователи с приоритетом 2:</h2>
          <ul>
            {usersWithPriority.length > 0 ? (
              usersWithPriority.map((userWithPriority, index) => (
                <li key={index}>
                  <p>{`${userWithPriority.login} - ${userWithPriority.position || 'Не указана'}`}</p>
                  <input
                    type="text"
                    value={userWithPriority.position || ''}  // обновил значение на пустую строку, если должность не указана
                    onChange={(e) => handleChangePositionForUser(userWithPriority.id, e.target.value)}
                  />
                  <button
                    onClick={() =>
                      handleChangePositionForUser(userWithPriority.id, userWithPriority.position)
                    }
                  >
                    Сохранить
                  </button>
                </li>
              ))
            ) : (
              <p>Нет пользователей с приоритетом 2.</p>
            )}
          </ul>
        </div>
      )}

      {user.priority !== 2 && (
        <div>
          <label>Должность:</label>
          <input type="text" value={position} onChange={handlePositionChange} />
          <button onClick={handleSave}>Сохранить</button>
        </div>
      )}

      {error && <p style={{ color: 'red' }}>{error}</p>}
    </div>
  );
}
