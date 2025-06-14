import axios from 'axios';
import {oidcService} from '../auth/oidc.service';

export interface TextMessage {
  id?: string;
  message: string;
}

export interface PaginatedResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}

class TextMessageService {
  private readonly baseUrl = 'http://localhost:8071';
  private readonly apiUrl = `${this.baseUrl}/api/textmessages`;

  async getAllTextMessages(page = 0, size = 10): Promise<PaginatedResponse<TextMessage>> {
    const sessionInfo = oidcService.getSessionInfo();
    if (!sessionInfo) {
      throw new Error('User not authenticated');
    }

    try {
      const response = await axios.get(`${this.apiUrl}`, {
        params: {
          page,
          size
        },
        headers: {
          Authorization: `Bearer ${sessionInfo.tokens.access}`
        }
      });
      return response.data;
    } catch (error) {
      console.error('Error fetching text messages:', error);
      throw error;
    }
  }

  async createTextMessage(message: string): Promise<string> {
    const sessionInfo = oidcService.getSessionInfo();
    if (!sessionInfo) {
      throw new Error('User not authenticated');
    }

    try {
      const response = await axios.post(
        this.apiUrl,
        { message },
        {
          headers: {
            Authorization: `Bearer ${sessionInfo.tokens.access}`
          }
        }
      );
      return response.data;
    } catch (error) {
      console.error('Error creating text message:', error);
      throw error;
    }
  }
}

export const textMessageService = new TextMessageService();
