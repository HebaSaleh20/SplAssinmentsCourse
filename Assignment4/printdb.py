from persistence import *

def main():
    #TODO: implement
    print('Activities')
    actlist=repo.get_all_activities()
    if actlist:
        print(*actlist,sep='\n')
    print('Branches')
    bralist=repo.get_all_branches()
    if bralist:
        print(*bralist,sep='\n')
    print('Employees')
    emplist=repo.get_all_employees()
    if emplist:
        print(*emplist,sep='\n')
    print('Products')
    prolist=repo.get_all_products()
    if prolist:
        print(*prolist,sep='\n')
    print('Suppliers')
    supplist=repo.get_all_suppliers()
    if supplist:
        print(*supplist,sep='\n')
    print('\nEmployees report')
    employees_report=repo.employees_report()
    for tuple in employees_report:
        print(*tuple,sep=' ')
    print('\nActivities report')
    act_report=repo.activities_report()
    print(*act_report,sep='\n')
   
    
    
    
    

if __name__ == '__main__':
    main()