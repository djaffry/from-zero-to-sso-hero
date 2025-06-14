import {Permission} from './permission';
import {getSessionInfoSync} from "@/auth/session.store.ts";

class PermissionCheckService {

  public hasViewTextMessagesPermission(): boolean {
    const userRoles = (getSessionInfoSync())?.user?.roles;
    return !!userRoles?.includes(Permission.VIEW_TEXT_MESSAGES);
  }

  public hasEditTextMessagesPermission(): boolean {
    const userRoles = (getSessionInfoSync())?.user?.roles;
    return !!userRoles?.includes(Permission.EDIT_TEXT_MESSAGES);
  }
}

export const permissionCheckService = new PermissionCheckService();
