"""
Unit tests for Spring Boot API Client
"""
import pytest
from unittest.mock import Mock, patch, MagicMock
import requests
from src.spring_boot_client import SpringBootClient


class TestSpringBootClient:
    """Test cases for SpringBootClient"""
    
    @pytest.fixture
    def client(self):
        """Create a test client instance"""
        return SpringBootClient(
            base_url='http://localhost:8080',
            api_key='test-api-key',
            timeout=30
        )
    
    @pytest.fixture
    def mock_response(self):
        """Create a mock response"""
        response = Mock()
        response.status_code = 200
        response.content = b'{"id": 1, "data": "test"}'
        response.json.return_value = {"id": 1, "data": "test"}
        response.raise_for_status = Mock()
        return response
    
    def test_init(self, client):
        """Test client initialization"""
        assert client.base_url == 'http://localhost:8080'
        assert client.api_key == 'test-api-key'
        assert client.timeout == 30
        assert 'Authorization' in client.session.headers
        assert client.session.headers['Authorization'] == 'Bearer test-api-key'
    
    @patch('src.spring_boot_client.requests.Session.request')
    def test_get_pending_invoices_success(self, mock_request, client, mock_response):
        """Test successful get pending invoices"""
        # Setup mock
        mock_response.json.return_value = [
            {
                'id': 1,
                'invoiceNumber': 'INV-001',
                'dueDate': '2024-01-15',
                'amount': 1000.00,
                'customerName': 'Test Customer',
                'customerEmail': 'test@example.com'
            }
        ]
        mock_request.return_value = mock_response
        
        # Execute
        result = client.get_pending_invoices_for_reminder()
        
        # Assert
        assert len(result) == 1
        assert result[0]['id'] == 1
        assert result[0]['invoiceNumber'] == 'INV-001'
        mock_request.assert_called_once()
    
    @patch('src.spring_boot_client.requests.Session.request')
    def test_get_pending_invoices_empty(self, mock_request, client, mock_response):
        """Test get pending invoices with empty response"""
        mock_response.json.return_value = []
        mock_request.return_value = mock_response
        
        result = client.get_pending_invoices_for_reminder()
        
        assert result == []
    
    @patch('src.spring_boot_client.requests.Session.request')
    def test_log_reminder_success(self, mock_request, client, mock_response):
        """Test successful log reminder"""
        mock_response.json.return_value = {
            'id': 1,
            'invoiceId': 123,
            'reminderType': 'GENTLE',
            'channel': 'EMAIL',
            'sentDate': '2024-01-15T10:00:00'
        }
        mock_request.return_value = mock_response
        
        result = client.log_reminder(
            invoice_id=123,
            reminder_type='GENTLE',
            channel='EMAIL'
        )
        
        assert result['id'] == 1
        assert result['invoiceId'] == 123
        assert result['reminderType'] == 'GENTLE'
        assert result['channel'] == 'EMAIL'
        
        # Verify request was made with correct payload
        call_args = mock_request.call_args
        assert call_args[1]['json'] == {
            'invoiceId': 123,
            'reminderType': 'GENTLE',
            'channel': 'EMAIL'
        }
    
    @patch('src.spring_boot_client.requests.Session.request')
    def test_http_error_handling(self, mock_request, client):
        """Test HTTP error handling"""
        error_response = Mock()
        error_response.status_code = 404
        error_response.text = 'Not Found'
        error_response.raise_for_status.side_effect = requests.exceptions.HTTPError(
            response=error_response
        )
        mock_request.return_value = error_response
        
        with pytest.raises(requests.exceptions.HTTPError):
            client.get_pending_invoices_for_reminder()
    
    @patch('src.spring_boot_client.requests.Session.request')
    def test_connection_error_handling(self, mock_request, client):
        """Test connection error handling"""
        mock_request.side_effect = requests.exceptions.ConnectionError("Connection failed")
        
        with pytest.raises(requests.exceptions.ConnectionError):
            client.get_pending_invoices_for_reminder()
    
    @patch('src.spring_boot_client.requests.Session.request')
    def test_timeout_error_handling(self, mock_request, client):
        """Test timeout error handling"""
        mock_request.side_effect = requests.exceptions.Timeout("Request timeout")
        
        with pytest.raises(requests.exceptions.Timeout):
            client.get_pending_invoices_for_reminder()


class TestCreateClient:
    """Test cases for create_client convenience function"""
    
    def test_create_client_defaults(self):
        """Test create_client with default parameters"""
        from src.spring_boot_client import create_client
        
        client = create_client()
        
        assert client.base_url == 'http://localhost:8080'
        assert client.api_key == ''
        assert client.timeout == 30
    
    def test_create_client_custom(self):
        """Test create_client with custom parameters"""
        from src.spring_boot_client import create_client
        
        client = create_client(
            base_url='http://api.example.com',
            api_key='custom-key',
            timeout=60
        )
        
        assert client.base_url == 'http://api.example.com'
        assert client.api_key == 'custom-key'
        assert client.timeout == 60

