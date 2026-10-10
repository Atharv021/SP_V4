import { Component, OnInit } from '@angular/core';
import { Ticket } from '../../models/ticket.model';
import { AuthService } from '../../services/auth.service';
import { TicketService } from '../../services/ticket.service';
import { UI } from '../ui-helpers';

// Home page for both roles.
// Client: overview of their own tickets. Manager: information about SupportSphere and shortcuts.
@Component({
  selector: 'app-home-page',
  templateUrl: './home-page.component.html',
  styleUrls: ['./home-page.component.css']
})
export class HomePageComponent implements OnInit {

  ui = UI;
  isManager = false;
  firstName = '';
  greeting = '';

  tickets: Ticket[] = [];
  recentTickets: Ticket[] = [];
  waitingOnYou: Ticket[] = [];
  openCount = 0;
  inProgressCount = 0;
  resolvedCount = 0;
  closedCount = 0;
  totalTickets = 0;
  activityDays: { dateStr: string, count: number }[] = [];
  maxActivity = 0;
  totalActivity = 0;
  loading = false;
  errorMessage = '';

  timeRange: '7' | '30' | '90' = '7';
  chartPoints: any[] = [];
  createdPath = '';
  createdArea = '';
  resolvedPath = '';
  chartMax = 4;
  yAxis: number[] = [];
  
  ticketsRaised = 0;
  resolvedInPeriod = 0;
  currentlyActive = 0;
  resolvedClosed = 0;
  activityContext = '';

  constructor(private authService: AuthService, private ticketService: TicketService) {}

  ngOnInit(): void {
    this.isManager = this.authService.isManager();
    this.firstName = this.authService.getUsername().split(' ')[0];

    const hour = new Date().getHours();
    if (hour < 12) {
      this.greeting = 'Good morning';
    } else if (hour < 17) {
      this.greeting = 'Good afternoon';
    } else {
      this.greeting = 'Good evening';
    }

    if (!this.isManager) {
      this.loadMyTickets();
    }
  }

  loadMyTickets(): void {
    this.loading = true;
    this.errorMessage = '';
    this.ticketService.getTicketsByUserId(this.authService.getUserId()).subscribe({
      next: (tickets) => {
        this.tickets = tickets.sort(UI.newestFirst);
        this.totalTickets = this.tickets.length;
        this.recentTickets = this.tickets.slice(0, 5);
        this.openCount = 0;
        this.inProgressCount = 0;
        this.resolvedCount = 0;
        this.closedCount = 0;
        this.waitingOnYou = [];
        for (const t of this.tickets) {
          if (t.status === 'Open') {
            this.openCount++;
          } else if (t.status === 'In Progress') {
            this.inProgressCount++;
            if (t.supportAgent && this.waitingOnYou.length < 3) {
              this.waitingOnYou.push(t);
            }
          } else if (t.status === 'Resolved') {
            this.resolvedCount++;
          } else if (t.status === 'Closed') {
            this.closedCount++;
          }
        }
        
        this.currentlyActive = this.openCount + this.inProgressCount;
        this.resolvedClosed = this.resolvedCount + this.closedCount;
        
        this.calculateActivity();
        this.loading = false;
      },
      error: (error) => {
        this.loading = false;
        this.errorMessage = UI.errorMessage(error, 'Could not load your tickets.');
      }
    });
  }

  changeTimeRange(range: '7' | '30' | '90'): void {
    this.timeRange = range;
    this.calculateActivity();
  }

  barHeight(value: number): string {
    return this.chartMax > 0 ? (value / this.chartMax * 100) + '%' : '0%';
  }

  calculateActivity(): void {
    this.chartPoints = [];
    const todayStr = UI.todayIso();
    const days = parseInt(this.timeRange, 10);
    
    let maxVal = 0;
    this.ticketsRaised = 0;
    this.resolvedInPeriod = 0;
    let mostActiveCreated = 0;
    let mostActiveDate = '';

    for (let i = days - 1; i >= 0; i--) {
      const day = this.addDays(todayStr, -i);
      let created = 0;
      let resolved = 0;

      for (const t of this.tickets) {
        if (t.createdDate && t.createdDate.startsWith(day)) { created++; }
        if (t.resolutionDate && t.resolutionDate.startsWith(day)) { resolved++; }
      }

      this.ticketsRaised += created;
      this.resolvedInPeriod += resolved;
      maxVal = Math.max(maxVal, created, resolved);
      
      if (created > mostActiveCreated) {
        mostActiveCreated = created;
        mostActiveDate = UI.shortDate(day);
      }

      // Compute label logic
      const is7 = this.timeRange === '7';
      const is30 = this.timeRange === '30';
      const is90 = this.timeRange === '90';
      
      let label = '';
      if (is7) {
        label = UI.shortDate(day);
      } else if (is30) {
        label = i % 5 === 0 || i === 0 ? UI.shortDate(day) : '';
      } else if (is90) {
        label = i % 15 === 0 || i === 0 ? UI.shortDate(day) : '';
      }

      this.chartPoints.push({
        label,
        fullDate: UI.formatDate(day),
        created,
        resolved
      });
    }

    this.chartMax = Math.max(4, Math.ceil(maxVal / 4) * 4);
    this.yAxis = [this.chartMax, this.chartMax * 0.75, this.chartMax * 0.5, this.chartMax * 0.25, 0];

    if (this.ticketsRaised === 0) {
      this.activityContext = "No tickets were created during this period.";
    } else if (mostActiveCreated > 1) {
      this.activityContext = `Your most active day was ${mostActiveDate}, with ${mostActiveCreated} tickets created.`;
    } else {
      this.activityContext = `You created ${this.ticketsRaised} ticket${this.ticketsRaised === 1 ? '' : 's'} and ${this.resolvedInPeriod} were resolved.`;
    }
  }

  private addDays(isoDate: string, days: number): string {
    const d = new Date(isoDate + 'T00:00:00Z');
    d.setUTCDate(d.getUTCDate() + days);
    return d.toISOString().substring(0, 10);
  }
}
