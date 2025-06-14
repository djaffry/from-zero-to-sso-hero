import {Permission} from './permission';
import {getSessionInfoSync} from "@/auth/session.store.ts";

class PermissionCheckService {

  public hasViewAccountInfoPermission(): boolean {
    const userRoles = (getSessionInfoSync())?.user?.roles;
    return !!userRoles?.includes(Permission.VIEW_ACCOUNT_INFO);
  }

  public hasEditAccountInfoPermission(): boolean {
    const userRoles = (getSessionInfoSync())?.user?.roles;
    return !!userRoles?.includes(Permission.EDIT_ACCOUNT_INFO);
  }
}

export const permissionCheckService = new PermissionCheckService();
