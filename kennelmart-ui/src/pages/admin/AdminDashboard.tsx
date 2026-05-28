/* eslint-disable react-hooks/set-state-in-effect */
import { useEffect, useState, useCallback } from 'react';
import { adminService } from '../../services/adminService';
import { reportService } from '../../services/reportService';
import type { User } from '../../types/auth';
import type { ProductListing } from '../../types/marketplace';
import type { ReportResponse } from '../../types/report';
import './AdminDashboard.css';

type Tab = 'users' | 'listings' | 'reports' | 'verifications';

export const AdminDashboard = () => {
  const [activeTab, setActiveTab] = useState<Tab>('users');
  const [users, setUsers] = useState<User[]>([]);
  const [listings, setListings] = useState<ProductListing[]>([]);
  const [reports, setReports] = useState<ReportResponse[]>([]);
  const [pendingVerifications, setPendingVerifications] = useState<User[]>([]);
  const [loading, setLoading] = useState(true);
  const [userFilter, setUserFilter] = useState({ verificationStatus: '', accountStatus: '' });
  const [listingFilter, setListingFilter] = useState({ status: '' });
  const [reportFilter, setReportFilter] = useState({ status: '' });

  const fetchUsers = useCallback(async () => {
    setLoading(true);
    try {
      const data = await adminService.getUsers({ ...userFilter, page: 0, size: 50 });
      setUsers(data.content);
    } catch (error) {
      console.error(error);
    } finally {
      setLoading(false);
    }
  }, [userFilter]);

  const fetchListings = useCallback(async () => {
    setLoading(true);
    try {
      const data = await adminService.getListings({ ...listingFilter, page: 0, size: 50 });
      setListings(data.content);
    } catch (error) {
      console.error(error);
    } finally {
      setLoading(false);
    }
  }, [listingFilter]);

  const fetchReports = useCallback(async () => {
    setLoading(true);
    try {
      const data = await adminService.getReports({ ...reportFilter, page: 0, size: 50 });
      setReports(data.content);
    } catch (error) {
      console.error(error);
    } finally {
      setLoading(false);
    }
  }, [reportFilter]);

  const fetchPendingVerifications = useCallback(async () => {
    setLoading(true);
    try {
      const data = await adminService.getUsers({ verificationStatus: 'PENDING', page: 0, size: 50 });
      setPendingVerifications(data.content);
    } catch (error) {
      console.error(error);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    if (activeTab === 'users') fetchUsers();
    if (activeTab === 'listings') fetchListings();
    if (activeTab === 'reports') fetchReports();
    if (activeTab === 'verifications') fetchPendingVerifications();
  }, [activeTab, fetchUsers, fetchListings, fetchReports, fetchPendingVerifications]);

  const handleSuspend = async (userId: string) => {
    if (confirm('Suspend this user?')) {
      await adminService.suspendUser(userId);
      fetchUsers();
    }
  };

  const handleActivate = async (userId: string) => {
    if (confirm('Activate this user?')) {
      await adminService.activateUser(userId);
      fetchUsers();
    }
  };

  const handleApproveListing = async (listingId: string) => {
    await adminService.approveListing(listingId);
    fetchListings();
  };

  const handleRejectListing = async (listingId: string) => {
    if (confirm('Reject this listing? It will be set to INACTIVE.')) {
      await adminService.rejectListing(listingId);
      fetchListings();
    }
  };

  const handleDeleteListing = async (listingId: string) => {
    if (confirm('Delete this listing? (Soft delete – it will be marked as DELETED)')) {
      await adminService.deleteListing(listingId);
      fetchListings();
    }
  };

  const handleResolveReport = async (reportId: string) => {
    await adminService.resolveReport(reportId);
    fetchReports();
  };

  const handleDismissReport = async (reportId: string) => {
    await adminService.dismissReport(reportId);
    fetchReports();
  };

  const handleMarkReviewing = async (reportId: string) => {
    await reportService.markReviewing(reportId);
    fetchReports();
  };

  const handleSuspendUserFromReport = async (userId: string, reportId: string) => {
    if (confirm('Suspend this user and resolve the report?')) {
      await reportService.suspendUserFromReport(reportId, userId);
      fetchReports();
    }
  };

  const handleDeleteListingFromReport = async (listingId: string, reportId: string) => {
    if (confirm('Delete this listing and resolve the report?')) {
      await reportService.deleteListingFromReport(reportId, listingId);
      fetchReports();
    }
  };

  const handleApproveVerification = async (userId: string) => {
    if (confirm('Approve this user’s verification?')) {
      await adminService.verifyUser(userId);
      fetchPendingVerifications();
    }
  };

  const handleRejectVerification = async (userId: string) => {
    if (confirm('Reject this user’s verification?')) {
      await adminService.rejectUserVerification(userId);
      fetchPendingVerifications();
    }
  };

  return (
    <div className="admin-dashboard">
      <h1>Admin Dashboard</h1>
      <div className="tabs">
        <button className={activeTab === 'users' ? 'active' : ''} onClick={() => setActiveTab('users')}>Users</button>
        <button className={activeTab === 'listings' ? 'active' : ''} onClick={() => setActiveTab('listings')}>Listings</button>
        <button className={activeTab === 'reports' ? 'active' : ''} onClick={() => setActiveTab('reports')}>Reports</button>
        <button className={activeTab === 'verifications' ? 'active' : ''} onClick={() => setActiveTab('verifications')}>Verifications</button>
      </div>

      {activeTab === 'users' && (
        <div className="tab-content">
          <div className="filters">
            <select onChange={(e) => setUserFilter(prev => ({ ...prev, verificationStatus: e.target.value }))}>
              <option value="">All Verification</option>
              <option value="PENDING">Pending</option>
              <option value="VERIFIED">Verified</option>
              <option value="REJECTED">Rejected</option>
            </select>
            <select onChange={(e) => setUserFilter(prev => ({ ...prev, accountStatus: e.target.value }))}>
              <option value="">All Account Status</option>
              <option value="ACTIVE">Active</option>
              <option value="SUSPENDED">Suspended</option>
            </select>
          </div>
          {loading ? (
            <div>Loading...</div>
          ) : (
            <div className="table-wrapper">
              <table className="data-table">
                <thead>
                  <tr>
                    <th>Name</th>
                    <th>Email</th>
                    <th>Verification</th>
                    <th>Account</th>
                    <th>Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {users.map(user => (
                    <tr key={user.id}>
                      <td>{user.name}</td>
                      <td>{user.email}</td>
                      <td><span className={`status ${user.verificationStatus.toLowerCase()}`}>{user.verificationStatus}</span></td>
                      <td><span className={`status ${user.accountStatus.toLowerCase()}`}>{user.accountStatus}</span></td>
                      <td>
                        {user.accountStatus === 'ACTIVE' ? (
                          <button onClick={() => handleSuspend(user.id)}>Suspend</button>
                        ) : (
                          <button onClick={() => handleActivate(user.id)}>Activate</button>
                        )}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>
      )}

      {activeTab === 'listings' && (
        <div className="tab-content">
          <div className="filters">
            <select onChange={(e) => setListingFilter({ status: e.target.value })}>
              <option value="">All Status</option>
              <option value="PENDING_APPROVAL">Pending Approval</option>
              <option value="ACTIVE">Active</option>
              <option value="INACTIVE">Inactive</option>
              <option value="SOLD_OUT">Sold Out</option>
              <option value="DELETED">Deleted</option>
            </select>
          </div>
          {loading ? (
            <div>Loading...</div>
          ) : (
            <div className="table-wrapper">
              <table className="data-table">
                <thead>
                  <tr>
                    <th>Title</th>
                    <th>Seller</th>
                    <th>Price</th>
                    <th>Status</th>
                    <th>Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {listings.map(listing => (
                    <tr key={listing.id}>
                      <td>{listing.title}</td>
                      <td>{listing.sellerName}</td>
                      <td>₱{listing.price}</td>
                      <td><span className={`status ${listing.status.toLowerCase()}`}>{listing.status}</span></td>
                      <td className="actions">
                        {listing.status === 'PENDING_APPROVAL' && (
                          <div className="button-group">
                            <button onClick={() => handleApproveListing(listing.id)} className="approve-btn">Approve</button>
                            <button onClick={() => handleRejectListing(listing.id)} className="reject-btn">Reject</button>
                          </div>
                        )}
                        {listing.status !== 'DELETED' && (
                          <button onClick={() => handleDeleteListing(listing.id)} className="delete">Delete</button>
                        )}
                        {listing.status === 'DELETED' && <span className="deleted-badge">Deleted</span>}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>
      )}

      {activeTab === 'reports' && (
        <div className="tab-content">
          <div className="filters">
            <select onChange={(e) => setReportFilter({ status: e.target.value })}>
              <option value="">All Status</option>
              <option value="PENDING">Pending</option>
              <option value="REVIEWING">Reviewing</option>
              <option value="RESOLVED">Resolved</option>
              <option value="DISMISSED">Dismissed</option>
            </select>
          </div>
          {loading ? (
            <div>Loading...</div>
          ) : (
            <div className="table-wrapper">
              <table className="data-table">
                <thead>
                  <tr>
                    <th>Reporter</th>
                    <th>Target Type</th>
                    <th>Target ID</th>
                    <th>Reason</th>
                    <th>Status</th>
                    <th>Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {reports.map(report => (
                    <tr key={report.id}>
                      <td>{report.reporterEmail}</td>
                      <td>{report.targetType}</td>
                      <td>{report.targetId}</td>
                      <td>{report.reason}</td>
                      <td><span className={`status ${report.status.toLowerCase()}`}>{report.status}</span></td>
                      <td className="actions">
                        {(report.status === 'PENDING' || report.status === 'REVIEWING') && (
                          <div className="button-group">
                            <button onClick={() => handleMarkReviewing(report.id)}>Review</button>
                            {report.targetType === 'USER' && (
                              <button onClick={() => handleSuspendUserFromReport(report.targetId, report.id)}>Suspend User</button>
                            )}
                            {report.targetType === 'LISTING' && (
                              <button onClick={() => handleDeleteListingFromReport(report.targetId, report.id)}>Delete Listing</button>
                            )}
                            <button onClick={() => handleResolveReport(report.id)}>Resolve</button>
                            <button onClick={() => handleDismissReport(report.id)}>Dismiss</button>
                          </div>
                        )}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>
      )}

      {activeTab === 'verifications' && (
        <div className="tab-content">
          <div className="filters">
            <button onClick={fetchPendingVerifications}>Refresh</button>
          </div>
          {loading ? (
            <div>Loading...</div>
          ) : (
            <div className="table-wrapper">
              <table className="data-table">
                <thead>
                  <tr>
                    <th>Name</th>
                    <th>Email</th>
                    <th>Student ID</th>
                    <th>Status</th>
                    <th>Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {pendingVerifications.map(user => (
                    <tr key={user.id}>
                      <td>{user.name}</td>
                      <td>{user.email}</td>
                      <td>{user.studentOrFacultyId}</td>
                      <td><span className={`status ${user.verificationStatus.toLowerCase()}`}>{user.verificationStatus}</span></td>
                      <td>
                        <div className="button-group">
                          <button onClick={() => handleApproveVerification(user.id)} className="approve-btn">Approve</button>
                          <button onClick={() => handleRejectVerification(user.id)} className="reject-btn">Reject</button>
                        </div>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>
      )}
    </div>
  );
};