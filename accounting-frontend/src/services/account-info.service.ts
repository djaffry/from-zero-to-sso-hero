import axios from 'axios';
import {oidcService} from '../auth/oidc.service';

export interface AccountInfo {
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

class AccountInfoService {
  private readonly baseUrl = 'http://localhost:8051';
  private readonly apiUrl = `${this.baseUrl}/api/accountinfos`;

  async getAllAccountInfos(page = 0, size = 10): Promise<PaginatedResponse<AccountInfo>> {
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
      console.error('Error fetching Account Info:', error);
      throw error;
    }
  }

  async createAccountInfo(message: string): Promise<string> {
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

export const accountInfoService = new AccountInfoService();
